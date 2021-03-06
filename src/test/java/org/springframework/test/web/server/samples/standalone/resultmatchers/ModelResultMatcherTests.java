/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.server.samples.standalone.resultmatchers;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

import javax.validation.Valid;

import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.samples.standalone.Person;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Examples of expectations on the content of the model prepared by the controller. 
 * 
 * @author Rossen Stoyanchev
 */
public class ModelResultMatcherTests {

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = standaloneSetup(new SampleController("a string value", 3, new Person("a name"))).build();
	}

	@Test
	public void testAttributeEqualTo() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(model().attribute("integer", 3))
			.andExpect(model().attribute("string", "a string value"));
		
		// Hamcrest Matchers..
		mockMvc.perform(get("/"))
			.andExpect(model().attribute("integer", equalTo(3)))
			.andExpect(model().attribute("string", equalTo("a string value")));
	}

	@Test
	public void testAttributeExists() throws Exception {
		mockMvc.perform(get("/")).andExpect(model().attributeExists("integer", "string", "person"));
		
		// Hamcrest Matchers..
		mockMvc.perform(get("/")).andExpect(model().attribute("integer", notNullValue()));
		mockMvc.perform(get("/")).andExpect(model().attribute("INTEGER", nullValue()));
	}

	@Test
	public void testAttributeHamcrestMatchers() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(model().attribute("integer", allOf(greaterThan(2), lessThan(4))))
			.andExpect(model().attribute("string", allOf(startsWith("a string"), endsWith("value"))))
			.andExpect(model().attribute("person", hasProperty("name", equalTo("a name"))));
	}

	@Test
	public void testHasErrors() throws Exception {
		mockMvc.perform(post("/persons")).andExpect(model().attributeHasErrors("person"));
	}

	@Test
	public void testHasNoErrors() throws Exception {
		mockMvc.perform(get("/")).andExpect(model().hasNoErrors());
	}

	
	@Controller
	@SuppressWarnings("unused")
	private static class SampleController {
		
		private final Object[] values;
		
		public SampleController(Object... values) {
			this.values = values;
		}

		@RequestMapping("/")
		public String handle(Model model) {
			for (Object value : this.values) {
				model.addAttribute(value);
			}
			return "view";
		}

		@RequestMapping(value="/persons", method=RequestMethod.POST)
		public String create(@Valid Person person, BindingResult result, Model model) {
			return "view";
		}
	}
	
}
