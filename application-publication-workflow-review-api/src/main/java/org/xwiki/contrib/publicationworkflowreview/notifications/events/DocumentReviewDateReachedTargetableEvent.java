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
package org.xwiki.contrib.publicationworkflowreview.notifications.events;

import java.util.Set;

import org.xwiki.eventstream.RecordableEvent;
import org.xwiki.eventstream.TargetableEvent;

/**
 * Targetable event triggered when the review date of published document is reached. The target is then the publishers
 * group corresponding to the published document.
 *
 * @version $Id$
 */
public class DocumentReviewDateReachedTargetableEvent implements RecordableEvent, TargetableEvent
{
    /**
     * Event name to be used in the components referring to that event.
     */
    // public static final String EVENT_NAME = "publication.workflow.review.date.reached";

    private final Set<String> target;

    /**
     * Default constructor.
     *
     * @param targets the list of people targeted by the event.
     */
    public DocumentReviewDateReachedTargetableEvent(Set<String> targets)
    {
        this.target = targets;
    }

    @Override
    public Set<String> getTarget()
    {
        return this.target;
    }

    @Override
    public boolean matches(Object otherEvent)
    {
        return otherEvent instanceof DocumentReviewDateReachedTargetableEvent;
    }
}
