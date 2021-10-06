package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import javax.swing.AbstractListModel

/**
 * Description of class LifeEventListModel
 * @created: 2019-11-07
 */
class LifeEventListModel(val events: MutableList<LifeEvent>): AbstractListModel<LifeEvent>() {

    override fun getElementAt(index: Int): LifeEvent {
        return events[index]
    }

    override fun getSize(): Int {
        return events.size
    }

    fun add(event: LifeEvent) {
        events.add(event)
        events.sortBy(LifeEvent::date)
        fireIntervalAdded(this, 0, events.size - 1)
    }

    fun remove(event: LifeEvent) {
        val index = events.indexOf(event)
        if (index > -1) {
            events.remove(event)
            fireIntervalRemoved(this, index, index)
        }
    }
}
