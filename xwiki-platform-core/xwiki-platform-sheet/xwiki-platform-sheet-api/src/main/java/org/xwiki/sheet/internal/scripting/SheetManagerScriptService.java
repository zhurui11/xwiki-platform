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
 */
package org.xwiki.sheet.internal.scripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.script.service.ScriptService;
import org.xwiki.sheet.SheetManager;
import org.xwiki.sheet.SheetManager.SheetDisplay;

/**
 * Exposes {@link SheetManager} to Velocity scripts.
 * 
 * @version $Id$
 */
@Component
@Named("sheet")
public class SheetManagerScriptService implements ScriptService
{
    /**
     * The component used to manage the sheets.
     */
    @Inject
    private SheetManager sheetManager;

    /**
     * The component used to check access rights on sheets.
     */
    @Inject
    private DocumentAccessBridge documentAccessBridge;

    /**
     * @param documentReference a reference to the document that is being rendered
     * @param action the action taken on the rendered document ('view', 'edit' etc.)
     * @return the list of {@code page} sheets available for the specified document on the specified action; a page
     *         sheet controls most of the generated HTML page; only one page sheet can be applied to a document
     */
    public DocumentReference getPageSheet(DocumentReference documentReference, String action)
    {
        if (documentAccessBridge.isDocumentViewable(documentReference)) {
            List<DocumentReference> pageSheets =
                filterViewable(sheetManager.getSheets(documentReference, action, SheetDisplay.PAGE));
            return pageSheets.isEmpty() ? null : pageSheets.get(0);
        }
        return null;
    }

    /**
     * @param documentReference a reference to the document that is being rendered
     * @param action the action taken on the rendered document ('view', 'edit' etc.)
     * @return the list of {@code inline} sheets available for the specified document on the specified action; multiple
     *         in-line sheets can be aggregated and displayed in the content area of the generated HTML page
     */
    public List<DocumentReference> getInlineSheets(DocumentReference documentReference, String action)
    {
        if (documentAccessBridge.isDocumentViewable(documentReference)) {
            return filterViewable(sheetManager.getSheets(documentReference, action, SheetDisplay.INLINE));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * @param classReference a reference to a document holding a class definition
     * @param action the action for which to retrieve the sheet
     * @return a sheet that can be used to render object of the specified class, for the specified action
     */
    public DocumentReference getClassSheet(DocumentReference classReference, String action)
    {
        if (documentAccessBridge.isDocumentViewable(classReference)) {
            DocumentReference sheetReference = sheetManager.getClassSheet(classReference, action);
            if (sheetReference != null && documentAccessBridge.isDocumentViewable(sheetReference)) {
                return sheetReference;
            }
        }
        return null;
    }

    /**
     * @param documentReferences a list of document references
     * @return only the references that point to documents that can be viewed by the current user
     */
    private List<DocumentReference> filterViewable(List<DocumentReference> documentReferences)
    {
        List<DocumentReference> viewable = new ArrayList<DocumentReference>();
        for (DocumentReference documentReference : documentReferences) {
            if (documentAccessBridge.isDocumentViewable(documentReference)) {
                viewable.add(documentReference);
            }
        }
        return viewable;
    }

    /**
     * Use this method if you want to apply a custom sheet to a document. The sheet can be automatically determined
     * based on the type of objects attached to the document (see {@link #bind(DocumentReference, DocumentReference)})
     * but this method allows you to overwrite this sheet with a custom one.
     * 
     * @param documentReference a reference to the document where to include the sheet
     * @param sheetReference specifies the sheet to be included
     */
    void bindDocumentSheet(DocumentReference documentReference, DocumentReference sheetReference)
    {
        if (documentAccessBridge.isDocumentEditable(documentReference)) {
            sheetManager.bindDocumentSheet(documentReference, sheetReference);
        }
    }

    /**
     * Use this method if you want to prevent a custom sheet from applying to a document. This is the opposite of
     * {@link #include(DocumentReference, DocumentReference)}
     * 
     * @param documentReference a reference to the document from which to exclude the sheet
     * @param sheetReference specifies the sheet to be excluded
     */
    void unbindDocumentSheet(DocumentReference documentReference, DocumentReference sheetReference)
    {
        if (documentAccessBridge.isDocumentEditable(documentReference)) {
            sheetManager.unbindDocumentSheet(documentReference, sheetReference);
        }
    }

    /**
     * Binds a XWiki class to a sheet so that whenever a document with an object of the specified type is rendered the
     * specified sheet is used.
     * 
     * @param classReference a XWiki class
     * @param sheetReference a class sheet that renders objects of the specified class
     */
    void bindClassSheet(DocumentReference classReference, DocumentReference sheetReference)
    {
        if (documentAccessBridge.isDocumentEditable(classReference)) {
            sheetManager.bindClassSheet(classReference, sheetReference);
        }
    }

    /**
     * Removes the binding between the specified XWiki class and the specified sheet. This is the opposite of
     * {@link #bind(DocumentReference, DocumentReference)}
     * 
     * @param classReference a XWiki class
     * @param sheetReference the class sheet that shouldn't be used anymore for rendering the objects of the specified
     *            class
     */
    void unbindClassSheet(DocumentReference classReference, DocumentReference sheetReference)
    {
        if (documentAccessBridge.isDocumentEditable(classReference)) {
            sheetManager.unbindClassSheet(classReference, sheetReference);
        }
    }
}
