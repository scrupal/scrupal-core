package scrupal.utils

import com.reactific.helpers.Patterns

import scala.io.{Codec, Source}
import scala.util.matching.Regex

object DomainNames {

  /** Top Level Domain Names
    * This loads the file conf/tlds-alapha-by-domain.txt as a list of strings. The file should be
    * downloaded from http://data.iana.org/TLD/tlds-alpha-by-domain.txt and updated periodically. It contains the
    * IANA registered top level domain names for use in matching domain names
    */
  lazy val tlds : Seq[String] = {
    val url = DomainNames.getClass.getResource("/tlds-alpha-by-domain.txt")
    Source.fromURL(url)(Codec.UTF8).getLines.drop(1).toSeq.sortWith { (str1,str2) => str1.length > str2.length }
  }

  /** Pattern For Top Level Domain matching
    * This makes an alternation pattern out of the strings in tlds, above.
    */
  lazy val tldPattern : Regex = {
    val bldr = new StringBuilder(8192)
    bldr.append("(?i)")
    tlds.foldLeft(bldr) { (b,e) => b.append(e).append("|") }
    new Regex(bldr.dropRight(1).toString)
  }

  import Patterns._

  val domainNameEntry : Regex = """[a-zA-Z0-9][-a-zA-Z0-9]*[a-zA-Z0-9]""".r
  val suffixedDomainNameEntry : Regex = join(domainNameEntry, """[.]""".r)
  val topTwoLevelsEntry : Regex = join(suffixedDomainNameEntry, capture(tldPattern,"tld") )
  val simpleDomainName : Regex = capture(alternate(topTwoLevelsEntry, "localhost".r),"top")
  val domainName : Regex = join(capture(between(0, 126, suffixedDomainNameEntry),"sub"), simpleDomainName)

  /** Match the parts of a domain name
    *
    * @param dn The domain name to match
    * @return A pair of options of strings. The first element is the domain name, the second element, if any
    *         is the subdomain name
    */
  def matchDomainName(dn: String) : (Option[String],Option[String]) = {
    dn match {
      case simpleDomainName(top,tld) =>
        Some(top) -> None
      case domainName(sub, top, tld) =>
        Some(top) -> Some(sub.dropRight(1))
      case "127.0.0.1" =>
        Some("localhost") -> None
      case "::1" =>
        Some("localhost") -> None
      case "fe80::1%lo0" =>
        Some("localhost") -> None
      case _ =>
        None -> None
    }
  }
}
