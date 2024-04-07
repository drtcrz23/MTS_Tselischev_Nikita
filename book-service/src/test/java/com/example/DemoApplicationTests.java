package com.example;

import com.example.bookService.BookApplication;
import com.example.bookService.DataBaseSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BookApplication.class)
class DemoApplicationTests extends DataBaseSuite {
	@Test
	void contextLoads() {
	}
}
