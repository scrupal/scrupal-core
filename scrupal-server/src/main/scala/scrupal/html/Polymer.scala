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

import scalatags.Text
import scalatags.text.Builder

/** Polymer Tags Object
  * This provides an easily importable accessor for the various classifications of polymer tags and attributes.
  * This is intended for server side (JVM) Text based construction of HTML using polymer attributes. See the
  * equivalent client side construct.
  */
object Polymer {

  object iron extends Text.Cap
      with PolymerIronTags[Builder,String,String]
      with PolymerIronAttributes[Builder,String,String]

    object paper extends Text.Cap
      with PolymerPaperTags[Builder,String,String]
}


