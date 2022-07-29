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
package org.xwiki.contrib.publicationworkflowreview.script;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.publicationworkflowreview.PublicationWorkflowReviewManager;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.QueryException;
import org.xwiki.script.service.ScriptService;

/**
 * Script service to acces the {@link PublicationWorkflowReviewManager}.
 *
 * @version $Id$
 */
@Component
@Named("publication.workflow.review")
public class PublicationWorkflowReviewScriptService implements ScriptService
{
    @Inject
    private PublicationWorkflowReviewManager publicationWorkflowReviewManager;

    /**
     * Get the list of the current ready to review published documents.
     * 
     * @return a list of DocumentReferences.
     * @throws QueryException in case of issue
     */
    public List<DocumentReference> getReadyToReviewDocuments() throws QueryException
    {
        return publicationWorkflowReviewManager.getReadyToReviewDocuments();
    }

    /**
     * Notify the publishers group of a published document that has its review date has been reached.
     * 
     * @param documentReference the published document reference
     */
    public void notifyDocumentPublishers(DocumentReference documentReference) throws Exception
    {
        publicationWorkflowReviewManager.notifyDocumentPublishers(documentReference);
    }
}
