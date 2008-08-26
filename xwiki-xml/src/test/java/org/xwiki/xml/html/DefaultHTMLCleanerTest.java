/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.xml.html;

import junit.framework.TestCase;
import org.xwiki.xml.XMLUtils;

/**
 * Unit tests for {@link org.xwiki.xml.html.DefaultHTMLCleaner}.
 *
 * @version $Id: $
 * @since 1.6M1
 */
public class DefaultHTMLCleanerTest extends TestCase
{
    private static final String HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<html><head /><body>";

    private static final String FOOTER = "</body></html>\n";

    private HTMLCleaner cleaner;

    protected void setUp()
    {
        this.cleaner = new DefaultHTMLCleaner();
    }

    public void testCloseUnbalancedTags()
    {
        assertHTML("<hr /><p>hello</p>", XMLUtils.toString(this.cleaner.clean("<hr><p>hello")));
    }

    public void testConversionsFromHTML()
    {
        assertHTML("<strong>bold</strong>", XMLUtils.toString(this.cleaner.clean("<b>bold</b>")));
        assertHTML("<em>italic</em>", XMLUtils.toString(this.cleaner.clean("<i>italic</i>")));
    }

    private void assertHTML(String expected, String actual)
    {
        assertEquals(HEADER + expected + FOOTER, actual);
    }
}
