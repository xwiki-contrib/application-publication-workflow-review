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
package org.xwiki.contrib.publicationworkflowreview.internal;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.workflowpublication.DocumentPublishingEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * @version $Id$
 */
@Component
@Named("publicationworkflowreviewlistener")
@Singleton
public class PublishedDocumentReviewDateInitializerListener implements EventListener
{
    private static final String WORKFLOW_REVIEW_CLASS_NAME = "PublicationWorkflowReviewClass";

    private static final EntityReference workflowReviewClassReference = new EntityReference(WORKFLOW_REVIEW_CLASS_NAME,
        EntityType.DOCUMENT, new EntityReference("PublicationWorkflowReviewCode", EntityType.SPACE));

    @Inject
    private Logger logger;

    @Override
    public String getName()
    {
        return "publicationworkflowreviewlistener";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new DocumentPublishingEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        try {
            XWikiContext xcontext = (XWikiContext) data;
            XWikiDocument document = (XWikiDocument) source;

            logger.debug("Published Document Review Date Initializer Listener Started ...");

            logger.debug("Published page : {}", document.getDocumentReference());

            // Attach the object of class 'PublicationWorkflowReviewCode.PublicationWorkflowReviewClass' to the
            // published document
            BaseObject object = document.getXObject(workflowReviewClassReference, true, xcontext);

            // Initialize the review date with the previous one if exists
            BaseObject previousObject = document.getOriginalDocument().getXObject(workflowReviewClassReference);

            if (previousObject != null && previousObject.getDateValue("reviewDate") != null) {
                object.setDateValue("reviewDate", previousObject.getDateValue("reviewDate"));
            }

            logger.debug("Published Document  Review Date Initializer Listener Finished ...");
        } catch (Exception e) {
            logger.error("Failure in the Published Document Review Date Initializer Listener.", e);
        }
    }
}
