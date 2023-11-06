package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE;

public class HotelIndexTest {
	private RestHighLevelClient client;

	@BeforeEach
	void setUp() {
		this.client = new RestHighLevelClient(RestClient.builder(
				HttpHost.create("http://1.12.221.184:9200")
		));
	}

	@AfterEach
	void tearDown() throws IOException {
		this.client.close();
	}

	@Test
	void testCreateHotelIndex() throws IOException {
		// 1.创建request对象
		CreateIndexRequest request = new CreateIndexRequest("hotel1");

		// 2.请求的dsl语句
		request.source(MAPPING_TEMPLATE, XContentType.JSON);

		// 3.发起请求,client.indices()获得所有的索引库操作方法
		client.indices().create(request, RequestOptions.DEFAULT);
	}

	@Test
	void deleteCreateHotelIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("hotel1");
		client.indices().delete(request, RequestOptions.DEFAULT);
	}

	@Test
	void existsCreateHotelIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("hotel1");
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}
}
