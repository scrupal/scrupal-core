package scrupal.utils

import org.specs2.mutable.Specification

class DomainNamesSpec extends Specification {

  "DomainNames" should {
    "recognize top level domain names" in {
      val result = DomainNames.tldPattern.findPrefixMatchOf("com")
      result.isDefined must beTrue
      result.get.source must beEqualTo("com")
    }
    "fail to recognize bogus top level domain names" in {
      val result = DomainNames.tldPattern.findPrefixMatchOf("fubar")
      result.isDefined must beFalse
    }
    "recognize simple domain names" in {
      val result = DomainNames.simpleDomainName.findPrefixMatchOf("foo.net")
      result.isDefined must beTrue
      result.get.source must beEqualTo("foo.net")
    }
    "fail to recognize non-domain names" in {
      DomainNames.simpleDomainName.findPrefixMatchOf("").isDefined must beFalse
      DomainNames.simpleDomainName.findPrefixMatchOf("foo net").isDefined must beFalse
      DomainNames.simpleDomainName.findPrefixMatchOf("#$%.net").isDefined must beFalse
      DomainNames.simpleDomainName.findPrefixMatchOf("ab_c.org").isDefined must beFalse
    }

    "match simple domain names" in {
      "foo.net" match {
        case DomainNames.simpleDomainName(top,tld) =>
          top must beEqualTo("foo.net")
          tld must beEqualTo("net")
        case _ =>
          failure("oops")
      }
      success("good")
    }
    "recognize 'reactific.com'" in {
      val result = DomainNames.matchDomainName("reactific.com")
      result._1 must beEqualTo(Some("reactific.com"))
      result._2 must beEqualTo(None)
    }

    "recognize 'admin.reactific.com'" in {
      DomainNames.matchDomainName("admin.reactific.com") must beEqualTo( Some("reactific.com") -> Some("admin") )
    }

    "recognize 'localhost'" in {
      DomainNames.matchDomainName("localhost") must beEqualTo (Some("localhost") -> None )
      DomainNames.matchDomainName("127.0.0.1") must beEqualTo (Some("localhost") -> None )
      DomainNames.matchDomainName("::1") must beEqualTo (Some("localhost") -> None )
      DomainNames.matchDomainName("fe80::1%lo0") must beEqualTo (Some("localhost") -> None )
    }
    "fail un unrecognized host name" in {
      DomainNames.matchDomainName("bad") must beEqualTo ( None → None )
    }
  }
}
