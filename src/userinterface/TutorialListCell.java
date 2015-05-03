/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import javafx.scene.control.ListCell;

/**
 *
 * @author CanFirtina
 */
public class TutorialListCell extends ListCell<TutorialItem>{
    
    @Override
    public void updateItem(TutorialItem item, boolean empty)
    {
        super.updateItem( item,empty);
        if( item != null)
        {
            TutorialCellItemController itemController = new TutorialCellItemController();
            itemController.setInfo( item);
            setGraphic(itemController.getCellData());
        }
    }
}
