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

package scrupal.core

import com.reactific.slickery.Storable._
import play.api.routing.sird._


trait EntityCollectionReactor extends Reactor
trait EntityInstanceReactor extends Reactor

trait EntityCreate extends EntityCollectionReactor {
  val description = "Create a specific instance of the entity and insert it in the entity collection."
}

trait EntityQuery extends EntityCollectionReactor {
  val description = "Query the entity collection for an entity of a certain id or containing certain data."
}

trait EntityRetrieve extends EntityCollectionReactor {
  val description = "Retrieve a specific instance of the entity from the entity collection."
}

trait EntityInfo extends EntityCollectionReactor {
  val description = "Get information about a specific instance of the entity from the entity collection."
}

trait EntityUpdate extends EntityCollectionReactor {
  val description = "Update a specific instance of the entity."
}

trait EntityDelete extends EntityCollectionReactor {
  val description = "Delete a specific instance from the entity collection."
}

trait EntityAdd extends EntityInstanceReactor {
  val description = "Create a facet on a specific entity in the collection."
}

trait EntityGet extends EntityInstanceReactor {
  val description = "Retrieve a facet from a specific entity in the collection."
}

trait EntityFacetInfo extends EntityInstanceReactor {
  val description = "Get information about a facet from a specific entity in the collection."
}

trait EntitySet extends EntityInstanceReactor {
  val description = "Update a facet from a specific entity in the collection."
}

trait EntityRemove extends EntityInstanceReactor {
  val description = "Delete a facet from a specific entity in the collection."
}

trait EntityFind extends EntityInstanceReactor {
  val description = "Query a specific entity in the collection for a facet of a certain id or containing certain data."
}

/** Router For Entities
  *
  * This decodes METHOD/path pairs into corresponding function calls to generate the corresponding Reactor that will
  * produce the desire Response for an entity.
  */
trait EntityProvider extends PluralProvider with Enablee {

  /** Plural (Collection) Routes
    * This value defines the routes for operations that affect the collection of entities
    */
  final val pluralRoutes: ReactionRoutes = {
    case GET(p"/${long(id)}/$rest<.*>") ⇒
      retrieve(id, rest)
    case GET(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$rest<.*>") ⇒
      retrieve(id, rest)
    case HEAD(p"/${long(id)}/$rest<.*>") ⇒
      info(id, rest)
    case HEAD(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$rest<.*>") ⇒
      info(id, rest)
    case OPTIONS(p"/$rest<.*>") ⇒
      query(rest)
    case POST(p"/$rest<.*>") ⇒
      create(rest)
    case PUT(p"/${long(id)}/$rest<.*>") ⇒
      update(id, rest)
    case PUT(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$rest<.*>") ⇒
      update(id, rest)
    case DELETE(p"/${long(id)}/$rest*") ⇒
      delete(id, rest)
    case DELETE(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$rest<.*>") ⇒
      delete(id, rest)
  }

  /** Singular (Instance) Routes
    * This value defines the routes for operations that affect a single isntance of an entity
    */
  final val singularRoutes: ReactionRoutes = {
    case GET(p"/${long(id)}/$facet/$facet_id/$rest<.*>") ⇒
      get(id, facet, facet_id, rest)
    case GET(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$facet/$facet_id/$rest<.*>") ⇒
      get(id, facet, facet_id, rest)
    case HEAD(p"/${long(id)}/$facet/$facet_id/$rest<.*>") ⇒
      facetInfo(id, facet, facet_id, rest)
    case HEAD(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$facet/$facet_id/$rest<.*>") ⇒
      facetInfo(id, facet, facet_id, rest)
    case OPTIONS(p"/${long(id)}/$facet/$rest<.*>") ⇒
      find(id, facet, rest)
    case OPTIONS(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$facet/$rest<.*>") ⇒
      find(id, facet, rest)
    case POST(p"/${long(id)}/$facet/$rest<.*>") ⇒
      add(id, facet, rest)
    case POST(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$facet/$rest<.*>") ⇒
      add(id, facet, rest)
    case PUT(p"/${long(id)}/$facet/$facet_id/$rest<.*>") ⇒
      set(id, facet, facet_id, rest)
    case PUT(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$facet/$facet_id/$rest<.*>") ⇒
      set(id, facet, facet_id, rest)
    case DELETE(p"/${long(id)}/$facet/$facet_id/$rest<.*>") ⇒
      remove(id, facet, facet_id, rest)
    case DELETE(p"/$id<[A-Za-z][-_.~a-zA-Z0-9]*>/$facet/$facet_id/$rest<.*>") ⇒
      remove(id, facet, facet_id, rest)
  }

  /** Create An Entity Instance */
  def create(details: String) : EntityCreate = {
    NoOpEntityCreate(pluralPrefix, details)
  }

  /** Query Command (OPTIONS/plural)
    *
    * @return The QueryReaction to generate the response for the Query request
    */
  def query(details: String) : EntityQuery = {
    NoOpEntityQuery(pluralPrefix, details)
  }

  /** Retrieve Command (GET/plural) - Retrieve an existing entity by its identifier
    * This is a command on the entity type's contain to retrieve a specific entity. The full instance should be
    * retrieved, including all retrievable facets.
    *
    * @return
    */
  def retrieve(instance_id: OIDType, details: String) : EntityRetrieve = {
    NoOpEntityRetrieveById(pluralPrefix, instance_id, details)
  }

  def retrieve(eName: String, details: String) : EntityRetrieve = {
    NoOpEntityRetrieveByName(pluralPrefix, eName, details)
  }

  def info(instance_id: OIDType, details: String) : EntityInfo = {
    NoOpEntityInfoById(pluralPrefix, instance_id, details)
  }

  def info(eName: String, details: String) : EntityInfo = {
    NoOpEntityInfoByName(pluralPrefix, eName, details)
  }

  def update(instance_id: Long, details: String) : EntityUpdate = {
    NoOpEntityUpdateById(pluralPrefix, instance_id, details)
  }

  def update(eName: String, details: String) : EntityUpdate = {
    NoOpEntityUpdateByName(pluralPrefix, eName, details)
  }

  def delete(instance_id: Long, details: String) : EntityDelete = {
    NoOpEntityDeleteById(pluralPrefix, instance_id, details)
  }

  def delete(eName: String, details: String) : EntityDelete = {
    NoOpEntityDeleteByName(pluralPrefix, eName, details)
  }

  def find(instance_id: Long, facet: String, details: String) : EntityFind = {
    NoOpEntityFindById(singularPrefix, instance_id, facet, details)
  }

  def find(eName: String, facet: String, details: String) : EntityFind = {
    NoOpEntityFindByName(singularPrefix, eName, facet, details)
  }

  def add(instance_id: Long, facet: String, details: String) : EntityAdd = {
    NoOpEntityAddById(singularPrefix, instance_id, facet, details)
  }

  def add(eName: String, facet: String, details: String) : EntityAdd = {
    NoOpEntityAddByName(singularPrefix, eName, facet, details)
  }

  def get(instance_id: Long, facet: String, facet_id: String, details: String) : EntityGet = {
    NoOpEntityGetById(singularPrefix, instance_id, facet, facet_id, details)
  }

  def get(eName: String, facet: String, facet_id: String, details: String) : EntityGet = {
    NoOpEntityGetByName(singularPrefix, eName, facet, facet_id, details)
  }

  def facetInfo(instance_id: Long, facet: String, facet_id: String, details: String) : EntityFacetInfo = {
    NoOpEntityFacetInfoById(singularPrefix, instance_id, facet, facet_id, details)
  }

  def facetInfo(eName: String, facet: String, facet_id: String, details: String) : EntityFacetInfo = {
    NoOpEntityFacetInfoByName(singularPrefix, eName, facet, facet_id, details)
  }

  def set(instance_id: Long, facet: String, facet_id: String, details: String) : EntitySet = {
    NoOpEntitySetById(singularPrefix, instance_id, facet, facet_id, details)
  }

  def set(eName: String, facet: String, facet_id: String, details: String) : EntitySet = {
    NoOpEntitySetByName(singularPrefix, eName, facet, facet_id, details)
  }

  def remove(instance_id: Long, facet: String, facet_id: String, details: String) : EntityRemove = {
    NoOpEntityRemoveById(singularPrefix, instance_id, facet, facet_id, details)
  }

  def remove(eName: String, facet: String, facet_id: String, details: String) : EntityRemove = {
    NoOpEntityRemoveByName(singularPrefix, eName, facet, facet_id, details)
  }
}

abstract class NoOpEntity(val what : String) extends UnimplementedReactorTrait {
  val oid : Option[Long] = None
}

case class NoOpEntityCreate(pName: String, details: String)
  extends NoOpEntity(s"$pName.create(details=$details)") with EntityCreate

case class NoOpEntityQuery(pName: String, details: String)
  extends NoOpEntity(s"$pName.query(details=$details)") with EntityQuery

case class NoOpEntityRetrieveById(pName: String, instance_id: OIDType, details: String)
  extends NoOpEntity(s"$pName.retrieveById(id=$instance_id,details=$details)") with EntityRetrieve

case class NoOpEntityRetrieveByName(pName: String, eName : String, details: String)
  extends NoOpEntity(s"$pName.retrieveByName(name=$eName,details=$details)") with EntityRetrieve

case class NoOpEntityInfoById(pName: String, instance_id: OIDType, details: String)
  extends NoOpEntity(s"$pName.infoById(id=$instance_id,details=$details)") with EntityInfo

case class NoOpEntityInfoByName(pName: String, eName: String, details: String)
  extends NoOpEntity(s"$pName.infoByName(name=$eName,details=$details)") with EntityInfo

case class NoOpEntityUpdateById(pName: String, instance_id: OIDType, details: String)
  extends NoOpEntity(s"$pName.updateById(id=$instance_id,details=$details)") with EntityUpdate

case class NoOpEntityUpdateByName(pName: String, eName: String, details: String)
  extends NoOpEntity(s"$pName.updateByName(name=$eName,details=$details)") with EntityUpdate

case class NoOpEntityDeleteById(pName: String, instance_id: OIDType, details: String)
  extends NoOpEntity(s"$pName.deleteById(id=$instance_id,details=$details)") with EntityDelete

case class NoOpEntityDeleteByName(pName: String, eName: String, details: String)
  extends NoOpEntity(s"$pName.deleteByName(name=$eName,details=$details)") with EntityDelete

case class NoOpEntityAddById(sName: String, instance_id: OIDType, facet: String, details: String)
  extends NoOpEntity(s"$sName.addById(id=$instance_id,facet=$facet,details=$details)") with EntityAdd

case class NoOpEntityAddByName(sName: String, eName: String, facet: String, details: String)
  extends NoOpEntity(s"$sName.addByName(name=$eName,facet=$facet,details=$details)") with EntityAdd

case class NoOpEntityGetById(sName: String, instance_id: OIDType, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.getById(id=$instance_id,facet=$facet,facet_id=$facet_id,details=$details)")
    with EntityGet

case class NoOpEntityGetByName(sName: String, eName: String, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.getByName(name=$eName,facet=$facet,facet_id=$facet_id,details=$details)") with EntityGet

case class NoOpEntityFacetInfoById(sName: String, instance_id: OIDType, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.facetInfoById(id=$instance_id,facet=$facet,facet_id=$facet_id,details=$details)")
    with EntityFacetInfo

case class NoOpEntityFacetInfoByName(sName: String, eName: String, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.facetInfoByName(name=$eName,facet=$facet,facet_id=$facet_id,details=$details)")
    with EntityFacetInfo

case class NoOpEntitySetById(sName: String, instance_id: OIDType, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.setById(id=$instance_id,facet=$facet,facet_id=$facet_id,details=$details)")
    with EntitySet

case class NoOpEntitySetByName(sName: String, eName: String, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.setByName(name=$eName,facet=$facet,facet_id=$facet_id,details=$details)") with EntitySet

case class NoOpEntityRemoveById(sName: String, instance_id: OIDType, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.removeById(id=$instance_id,facet=$facet,facet_id=$facet_id,details=$details)")
    with EntityRemove

case class NoOpEntityRemoveByName(sName: String, eName: String, facet: String, facet_id: String, details: String)
  extends NoOpEntity(s"$sName.removeByName(name=$eName,facet=$facet,facet_id=$facet_id,details=$details)")
    with EntityRemove

case class NoOpEntityFindById(sName: String, instance_id: OIDType, facet: String, details: String)
  extends NoOpEntity(s"$sName.findById(id=$instance_id,facet=$facet,details=$details)") with EntityFind

case class NoOpEntityFindByName(sName: String, eName: String, facet: String, details: String)
  extends NoOpEntity(s"$sName.findByName(name=$eName,facet=$facet,details=$details)") with EntityFind

