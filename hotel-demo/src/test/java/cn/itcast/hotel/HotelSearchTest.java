package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
public class HotelSearchTest {
	private RestHighLevelClient client;

	@BeforeEach
	void setUp() {
		this.client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://1.12.221.184:9200")));
	}

	@AfterEach
	void tearDown() throws IOException {
		this.client.close();
	}

	@Test
	void testMatchAllQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source().query(QueryBuilders.matchAllQuery());

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		// 处理结果
		printResponse(search);
	}

	@Test
	void testMatchQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source().query(QueryBuilders.matchQuery("all", "如家"));

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		printResponse(search);
	}

	@Test
	void testMultiMatchQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source().query(QueryBuilders.multiMatchQuery("如家", "business", "name"));

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		printResponse(search);
	}

	@Test
	void testTermQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source().query(QueryBuilders.termQuery("price", "336"));

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		printResponse(search);
	}

	@Test
	void testRangeQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source().query(QueryBuilders.rangeQuery("price").gte(300).lte(400));

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		printResponse(search);
	}

	@Test
	void testBooleanQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		builder.must(QueryBuilders.termQuery("brand", "如家"));
		builder.filter(QueryBuilders.rangeQuery("price").gte(300));

		request.source().query(builder);

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		printResponse(search);
	}

	@Test
	void testPageSortQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source()
				.query(QueryBuilders.matchAllQuery())
				.sort("price", SortOrder.ASC)
				.from(10)
				.size(20);

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		// 处理结果
		printResponse(search);
	}

	@Test
	void testHighlightQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source().query(QueryBuilders.matchQuery("all", "如家"));

		//高亮
		request.source().highlighter(new HighlightBuilder()
				.field("name").requireFieldMatch(false));

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		printResponse(search);
	}

	public static void printResponse(SearchResponse search) {
		// 处理结果
		SearchHits searchHits = search.getHits();
		long count = searchHits.getTotalHits().value;
		System.out.println("一共有:" + count + "条数据");
		SearchHit[] hits = searchHits.getHits();
		for (SearchHit hit : hits) {
			HotelDoc hotelDoc = JSON.parseObject(hit.getSourceAsString(), HotelDoc.class);
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			if (!CollectionUtils.isEmpty(highlightFields)) {
				HighlightField highlightField = highlightFields.get("name");
				if (highlightField != null) {
					String name = highlightField.getFragments()[0].toString();
					System.out.println("高亮：" + name);
					hotelDoc.setName(name);
				}
			}
			System.out.println("hotelDoc=" + hotelDoc);
		}
	}
}
