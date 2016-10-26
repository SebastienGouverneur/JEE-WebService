package dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import bean.IPersonFactory;
import bean.Person;
import dao.IPersonDao;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
public class PersonDaoBdTest {
	
	@Autowired
	IPersonDao dao;
	
	@Autowired
	IPersonDao dao2;
	
	@Autowired
	IPersonFactory personFactory;
	
	
	@Test
	public void test() {
		System.out.println("coucou");
		//Assert.assertNotNull(dao);
		//Assert.assertNotNull(dao.getConnection());
		
		Person p1 = personFactory.getPerson();
		Person p2 = personFactory.getPerson();
	System.out.println(p1+" "+p2);
	}

}
