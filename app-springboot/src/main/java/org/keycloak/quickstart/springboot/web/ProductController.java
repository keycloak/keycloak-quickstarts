/*
 * Copyright 2012-2016 the original author or authors.
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

package org.keycloak.quickstart.springboot.web;

import java.security.Principal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CachePolicy;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.quickstart.springboot.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@CacheControl(policy = CachePolicy.NO_CACHE)
public class ProductController {

	@Autowired
	private ProductService productService;
        
        private @Autowired HttpServletRequest request;

	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public String handleCustomersRequest(Principal principal, Model model) {
			model.addAttribute("products", productService.getProducts());
			model.addAttribute("principal",  principal);
                        String logoutUri = KeycloakUriBuilder.fromUri("http://localhost:8180/auth").path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
            .queryParam("redirect_uri", "http://localhost:8080/products").build("quickstart").toString();
                        model.addAttribute("logout",  logoutUri);
			return "products";
	}
        
        @RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String handleLogoutt() throws ServletException {
            request.logout();
            return "landing";
        }
        
        @RequestMapping(value = "/", method = RequestMethod.GET)
	public String landing() throws ServletException {
            return "landing";
        }

}
