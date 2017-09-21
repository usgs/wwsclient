package gov.usgs.volcanoes.wwsclient;

/**
 * Holder for winston protocol version number.
 * 
 * @author Tom Parker
 *
 */
public class VersionHolder {
  /** winston protocol version */
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
      value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "Read elsewhere")
  public int version = -1;
}
