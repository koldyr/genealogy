package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Sex
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.text.SimpleDateFormat
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * Description of class EditPersonDialog
 * @created: 2019-10-27
 */
class EditPersonDialog(owner: Frame?, private val person: Person) : JDialog(owner, "Edit Person", true) {
    init {
        contentPane.layout = GridBagLayout()

        val lblId = JLabel("Id:")
        val txtId = JTextField(person.id.toString())
        txtId.isEditable = false

        val name: PersonNames = person.name!!;
        val lblName = JLabel("Name:")
        val txtName = JTextField(name.name)
        val txtMiddle = JTextField(name.middle)
        val txtLast = JTextField(name.last)
        val txtMaiden = JTextField(name.maiden)

        val lblSex = JLabel("Sex:")
        val cmbSex = JComboBox(Sex.values())
        cmbSex.selectedItem = person.sex


        val lblBirth = JLabel("Birth:")
        val txtBirth = JFormattedTextField(SimpleDateFormat("yyyy-MM-dd"))
//        txtBirth.value = getDate(person.birth)
        val txtBirthPlace = JTextField(person.place)

        rootPane.border = EmptyBorder(10, 10, 10, 10)
        contentPane.add(lblId, GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(txtId, GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0));

        contentPane.add(lblName, GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(txtName, GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0));
        if (person.sex == Sex.FEMALE) {
            contentPane.add(txtMaiden, GridBagConstraints(2, 1, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0));
        }

        contentPane.add(lblSex, GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(cmbSex, GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0));

        contentPane.add(lblBirth, GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0));
        contentPane.add(txtBirth, GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0));
        contentPane.add(txtBirthPlace, GridBagConstraints(2, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0));

        pack()
        setLocationRelativeTo(null)
    }

    private fun getDate(event: LifeEvent?) = event?.date
}
