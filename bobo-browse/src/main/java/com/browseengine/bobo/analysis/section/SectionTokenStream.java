/**
 * 
 */
package com.browseengine.bobo.analysis.section;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

/**
 * This class augments a token stream by attaching a section id as payloads.
 *
 */
public final class SectionTokenStream extends TokenFilter
{
  private BytesRef _payload;
  private PayloadAttribute _payloadAtt;
  
  public SectionTokenStream(TokenStream tokenStream, int sectionId)
  {
    super(tokenStream);
    _payloadAtt = (PayloadAttribute)addAttribute(PayloadAttribute.class);
    _payload = encodeIntPayload(sectionId);
  }

  public boolean incrementToken() throws IOException
  {
    if(input.incrementToken())
    {
      _payloadAtt.setPayload(_payload);
      return true;
    }
    return false;
  }

  static public BytesRef encodeIntPayload(int id)
  {
    byte[] data = new byte[4];
    int off = data.length;

    do
    {
      data[--off] = (byte)(id);
      id >>>= 8;
    }
    while(id > 0);
    
    return new BytesRef(data, off, data.length - off);
  }
  
  static public int decodeIntPayload(BytesRef payload)
  {
    return decodeIntPayload(payload.bytes, payload.offset, payload.length);
  }
  
  static public int decodeIntPayload(byte[] data, int off, int len)
  {
    int endOff = off + len;
    int val = 0;
    while(off < endOff)
    {
      val <<= 8;
      val += (data[off++] & 0xFF);
    }
    return val;
  }
}
