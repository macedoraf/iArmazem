package br.com.ion.iarmazem.model;

import java.util.ArrayList;

public class ArmazemModel {

    public ArmazemModel(ArrayList<ItemArmazemModel> itens) {
        this.itens = itens;
    }

    private ArrayList<ItemArmazemModel> itens;

    public int getItensSize() {
        int value = 0;
        if(itens != null) {
            for (ItemArmazemModel itemArmazemModel : itens) {
                value = value + itemArmazemModel.getAmount();
            }
        }
        return value;
    }

    public void addItem(String id, int amount) {
        for (ItemArmazemModel item : itens) {
            if(item.getID().equalsIgnoreCase(id)) {
                item.addQuantidade(amount);
                return;
            }
        }
    }

    public boolean hasItem(String id) {
        for (ItemArmazemModel item : itens) {
            if(item.getID().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public int getAmountItemByID(String id) {
        int value = 0;
        if(itens != null) {
            for (ItemArmazemModel itemArmazemModel : itens) {
                if(itemArmazemModel.getID().equalsIgnoreCase(id)) {
                    value = value + itemArmazemModel.getAmount();
                }
            }
        }
        return value;
    }

    public void setAmountItemByID(String id,int amount) {
        int value = 0;
        if(itens != null) {
            for (ItemArmazemModel itemArmazemModel : itens) {
                if(itemArmazemModel.getID().equalsIgnoreCase(id)) {
                    itemArmazemModel.setAmount(amount);
                }
            }
        }
    }

    public int getPriceItemByID(String id) {
        int value = 0;
        if(itens != null) {
            for (ItemArmazemModel itemArmazemModel : itens) {
                if(itemArmazemModel.getID().equalsIgnoreCase(id)) {
                    return itemArmazemModel.getPrice();
                }
            }
        }
        return 0;
    }

    public ArrayList<ItemArmazemModel> getItens() {
        return itens;
    }

    public void setItens(ArrayList<ItemArmazemModel> itens) {
        this.itens = itens;
    }
}
