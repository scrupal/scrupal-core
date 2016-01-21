/**********************************************************************************************************************
  * This file is part of Scrupal, a Scalable Reactive Web Application Framework for Content Management                 *
  *                                                                                                                    *
  * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
  *                                                                                                                    *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
  * with the License. You may obtain a copy of the License at                                                          *
  *                                                                                                                    *
  *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
  *                                                                                                                    *
  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
  * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
  * the specific language governing permissions and limitations under the License.                                     *
  **********************************************************************************************************************/
package scrupal.html

import scrupal.core.SiteData

class AdministrationSpec extends ValidatingSpecification("Administration") {

  implicit val ec = scrupal.executionContext

  "Administration" should {
    "have an introduction page" in {
      val future = Administration.page(context, Administration.introduction, Map(1L → "One", 2L → "Two"))
      val page = await(future)
      page.contains("<p>Welcome") must beTrue
      page.contains("Administration Help") must beTrue
      validate("Admin Introduction", page)
    }
    "have a site page" in {
      val pageContent = Administration.site(SiteData("Site","localhost"))()
      val future = Administration.page(context, pageContent, Map(1L → "One", 2L → "Two"))
      val page = await(future)
      validate("Admin Site", page)
    }
    "have a module page" in {
      val pageContent = Administration.module
      val future = Administration.page(context, pageContent, Map(1L → "One", 2L → "Two"))
      val page = await(future)
      validate("Admin Module", page)
    }
  }
}
