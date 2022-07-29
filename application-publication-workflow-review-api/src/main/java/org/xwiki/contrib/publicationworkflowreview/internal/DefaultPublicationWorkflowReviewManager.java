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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.context.Execution;
import org.xwiki.contrib.publicationworkflowreview.notifications.events.DocumentReviewDateReachedTargetableEvent;
import org.xwiki.contrib.publicationworkflowreview.PublicationWorkflowReviewManager;
import org.xwiki.observation.ObservationManager;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;
import org.xwiki.workflowpublication.PublicationRoles;
import org.xwiki.workflowpublication.WorkflowConfigManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Default implementation of {@link PublicationWorkflowReviewManager}.
 *
 * @version $Id$
 */
@Component
@Singleton
public class DefaultPublicationWorkflowReviewManager implements PublicationWorkflowReviewManager
{
    /**
     * Default event source.
     */
    static final String EVENT_SOURCE = "org.xwiki.contrib:application-publication-workflow-review-api";

    /**
     * Resolver for document references.
     */
    @Inject
    @Named("current")
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Inject
    private EntityReferenceSerializer<String> serializer;

    @Inject
    private QueryManager queryManager;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private WorkflowConfigManager workflowConfigManager;

    @Inject
    private PublicationRoles publicationRoles;

    @Inject
    private Execution execution;

    @Override
    public List<DocumentReference> getReadyToReviewDocuments() throws QueryException
    {
        List<DocumentReference> results = new ArrayList<DocumentReference>();

        String queryString = ", BaseObject as obj1, StringProperty as statusProp, IntegerProperty istargetProp, "
            + "BaseObject as obj2, " + "DateProperty reviewDateProp where doc.fullName = obj1.name and "
            + "obj1.className = 'PublicationWorkflow.PublicationWorkflowClass' "
            + "and obj1.id=statusProp.id.id and statusProp.id.name='status' and statusProp.value='published' "
            + "and obj1.id=istargetProp.id.id and istargetProp.id.name='istarget' and istargetProp.value=1 "
            + "and doc.fullName = obj2.name and "
            + "obj2.className = 'PublicationWorkflowReviewCode.PublicationWorkflowReviewClass'"
            + " and obj2.id=reviewDateProp.id.id and reviewDateProp.id.name='reviewDate' and "
            + "day(reviewDateProp.value) = :day and month(reviewDateProp.value) = :month "
            + "and year(reviewDateProp.value) = :year";
        Query query = queryManager.createQuery(queryString, Query.HQL);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        query.bindValue("day", day);
        query.bindValue("month", month);
        query.bindValue("year", year);

        for (Object docFullName : query.execute()) {
            results.add(documentReferenceResolver.resolve((String) docFullName));
        }

        return results;
    }

    @Override
    public void notifyDocumentPublishers(DocumentReference documentReference) throws Exception
    {
        XWikiContext xcontext = getXContext();

        XWikiDocument document = xcontext.getWiki().getDocument(documentReference, xcontext);
        String publishersGroup = getDocumentPublishersGroup(document);
        Set<String> target = new HashSet<>();
        target.add(publishersGroup);
        DocumentReviewDateReachedTargetableEvent event = new DocumentReviewDateReachedTargetableEvent(target);
        // Fire the event
        observationManager.notify(event, EVENT_SOURCE, document);
    }

    private String getDocumentPublishersGroup(XWikiDocument document) throws Exception
    {
        XWikiContext xcontext = getXContext();

        String publishersGroup = publicationRoles
            .getValidators(workflowConfigManager.getWorkflowConfigForWorkflowDoc(document, xcontext), xcontext);

        // resolve the publishersGroup and after serialize it in order to have the wiki part added on the string
        // reference of the group
        // because the notification will not be sent if the wiki part is missing on the string representation of the
        // publishers group

        return serializer.serialize(documentReferenceResolver.resolve(publishersGroup));
    }

    private XWikiContext getXContext()
    {
        return (XWikiContext) execution.getContext().getProperty("xwikicontext");
    }
}
