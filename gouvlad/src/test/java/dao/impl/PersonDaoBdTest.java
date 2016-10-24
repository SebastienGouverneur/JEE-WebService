package dao.impl;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.IPersonDao;


public class PersonDaoBdTest {
	
	@Autowired
	IPersonDao dao;
	
	
	@Before
	public void init(){
		 String conf = "spring.xml";
	        AbstractApplicationContext context = new ClassPathXmlApplicationContext(conf);
	        context.registerShutdownHook();

	}
	
	@Test
	public void test() {
		Assert.assertNotNull(dao);
	}

}
