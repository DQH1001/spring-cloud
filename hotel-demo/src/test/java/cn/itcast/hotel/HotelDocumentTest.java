package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.impl.HotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class HotelDocumentTest {
	@Autowired
	private HotelService hotelService;

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
	void testAddDocument() throws IOException {
		Hotel hotel = hotelService.getById(36934);
		// 1.创建request对象
		IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());

		// 2.请求的dsl语句
		request.source(JSON.toJSONString(hotel), XContentType.JSON);

		// 3.发起请求
		client.index(request, RequestOptions.DEFAULT);
	}

	@Test
	void testGetDocument() throws IOException {
		GetRequest request = new GetRequest("hotel", "36934");
		GetResponse response = client.get(request, RequestOptions.DEFAULT);
		String json = response.getSourceAsString();
		HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
		System.out.println(hotelDoc);
	}

	@Test
	void testUpdateDocument() throws IOException {
		UpdateRequest request = new UpdateRequest("hotel", "36934");
		request.doc(
				"starName","四钻",
				"price",180
		);
		client.update(request, RequestOptions.DEFAULT);
	}

	@Test
	void testDeleteDocument() throws IOException {
		DeleteRequest request = new DeleteRequest("hotel", "36934");
		client.delete(request, RequestOptions.DEFAULT);
	}

	@Test
	void testbulkDocument() throws IOException {
		List<Hotel> hotels = hotelService.list();
		BulkRequest request = new BulkRequest();
		for (Hotel hotel : hotels) {
			HotelDoc doc = new HotelDoc(hotel);
			request.add(new IndexRequest("hotel")
					.id(doc.getId().toString())
					.source(JSON.toJSONString(doc), XContentType.JSON)
			);
		}
		client.bulk(request, RequestOptions.DEFAULT);
	}
}
