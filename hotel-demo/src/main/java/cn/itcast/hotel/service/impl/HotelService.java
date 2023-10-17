package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
	@Autowired
	private RestHighLevelClient client;

	@Override
	public PageResult search(RequestParams requestParams) {
		try {
			// 准备request
			SearchRequest request = new SearchRequest("hotel");

			// 准备dsl
			buildQuery(requestParams, request);

			// 分页
			int page = requestParams.getPage();
			int size = requestParams.getSize();
			request.source().from((page - 1) * size).size(size);

			// 排序
			String location = requestParams.getLocation();
			if (!StringUtils.isEmpty(location)) {
				request.source().sort(SortBuilders
						.geoDistanceSort("location", new GeoPoint(location))
						.order(SortOrder.ASC)
						.unit(DistanceUnit.KILOMETERS)
				);
			}

			// 准备response
			SearchResponse response = client.search(request, RequestOptions.DEFAULT);
			return getResult(response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, List<String>> filters(RequestParams params) {
		try {
			SearchRequest request = new SearchRequest("hotel");
			buildQuery(params, request);
			request.source().size(0);
			request.source().aggregation(AggregationBuilders
					.terms("brandAggs")
					.field("brand")
					.size(100));
			request.source().aggregation(AggregationBuilders
					.terms("cityAggs")
					.field("city")
					.size(100));
			request.source().aggregation(AggregationBuilders
					.terms("starNameAggs")
					.field("starName")
					.size(100));
			SearchResponse response = client.search(request, RequestOptions.DEFAULT);

			Map<String, List<String>> result = new HashMap<>();
			List<String> brandAggs = getAggregation(response, "brandAggs");
			result.put("brand", brandAggs);
			List<String> cityAggs = getAggregation(response, "cityAggs");
			result.put("city", cityAggs);
			List<String> starNameAggs = getAggregation(response, "starNameAggs");
			result.put("starName", starNameAggs);

			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<String> getAggregation(SearchResponse response, String name) {
		List<String> list = new ArrayList<>();
		Aggregations aggregations = response.getAggregations();
		Terms aggs = aggregations.get(name);
		List<? extends Terms.Bucket> buckets = aggs.getBuckets();
		for (Terms.Bucket bucket : buckets) {
			list.add(bucket.getKeyAsString());
		}
		return list;
	}

	private static void buildQuery(RequestParams requestParams, SearchRequest request) {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		// 关键字搜索
		String key = requestParams.getKey();
		if (StringUtils.isEmpty(key)) {
			boolQuery.must(QueryBuilders.matchAllQuery());
		} else {
			boolQuery.must(QueryBuilders.matchQuery("all", key));
		}

		// 城市
		String city = requestParams.getCity();
		if (!StringUtils.isEmpty(city)) {
			boolQuery.filter(QueryBuilders.termQuery("city", city));
		}

		// 星级
		String starName = requestParams.getStarName();
		if (!StringUtils.isEmpty(starName)) {
			boolQuery.filter(QueryBuilders.termQuery("starName", starName));
		}

		// 品牌
		String brand = requestParams.getBrand();
		if (!StringUtils.isEmpty(brand)) {
			boolQuery.filter(QueryBuilders.termQuery("brand", brand));
		}

		// 价格
		Integer minPrice = requestParams.getMinPrice();
		Integer maxPrice = requestParams.getMaxPrice();
		if (minPrice != null && maxPrice != null) {
			boolQuery.filter(QueryBuilders.rangeQuery("price")
					.gte(minPrice)
					.lte(maxPrice));
		}

		// function score
		FunctionScoreQueryBuilder functionScoreQuery =
				QueryBuilders.functionScoreQuery(
						// 原始查询
						boolQuery,
						// function score数组
						new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
								// 一个具体的function score
								new FunctionScoreQueryBuilder.FilterFunctionBuilder(
										// 过滤条件
										QueryBuilders.termQuery("isAD", true),
										// 算分函数
										ScoreFunctionBuilders.weightFactorFunction(10)
								)
						});
		request.source().query(functionScoreQuery);
	}

	private PageResult getResult(SearchResponse search) {
		PageResult result = new PageResult();
		List<HotelDoc> docs = new ArrayList<>();
		// 处理结果
		SearchHits searchHits = search.getHits();
		long count = searchHits.getTotalHits().value;
		SearchHit[] hits = searchHits.getHits();
		for (SearchHit hit : hits) {
			HotelDoc hotelDoc = JSON.parseObject(hit.getSourceAsString(), HotelDoc.class);
			// 距离
			Object[] sortValues = hit.getSortValues();
			if (sortValues != null && sortValues.length > 0) {
				hotelDoc.setDistance(sortValues[0]);
			}

			//高亮
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			if (!CollectionUtils.isEmpty(highlightFields)) {
				HighlightField highlightField = highlightFields.get("name");
				if (highlightField != null) {
					String name = highlightField.getFragments()[0].toString();
					System.out.println("高亮：" + name);
					hotelDoc.setName(name);
				}
			}
			docs.add(hotelDoc);
		}
		result.setHotels(docs);
		result.setTotal(count);
		return result;
	}
}
