package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class HotelAggregationTest {
	private RestHighLevelClient client;

	@BeforeEach
	void setUp() {
		this.client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://43.139.239.12:9200")));
	}

	@AfterEach
	void tearDown() throws IOException {
		this.client.close();
	}

	@Test
	void testMatchAllQuery() throws IOException {
		SearchRequest request = new SearchRequest("hotel");
		request.source().query(QueryBuilders.matchAllQuery());

		request.source().size(0);
		request.source().aggregation(AggregationBuilders
				.terms("brandAggs")
				.field("brand")
				.size(20)
		);

		// 发起请求
		SearchResponse search = client.search(request, RequestOptions.DEFAULT);

		// 处理结果
		Aggregations aggregations = search.getAggregations();
		Terms brandTerms = aggregations.get("brandAggs");
		List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
		for (Terms.Bucket bucket : buckets) {
			String key = bucket.getKeyAsString();
			long count = bucket.getDocCount();
			System.out.println(key + " " + count);
		}
	}
}
