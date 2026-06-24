/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item;

import com.rs2.model.item.ItemContainerTab;
import com.rs2.model.item.ItemContainerType;
import com.rs2.model.item.ItemStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ItemContainer {
    private int capacity;
    private ItemStack[] items;
    private List updateListeners = new LinkedList();
    private ArrayList tabs = new ArrayList();
    private int tabLimit;
    private ItemContainerType containerType;
    private boolean updatesEnabled = true;

    public ItemContainer(ItemContainerType itemContainerType, int n) {
        this.containerType = itemContainerType;
        this.capacity = n;
        this.items = new ItemStack[n];
    }

    public ItemContainer(ItemContainerType itemContainerType, int n, int n2) {
        this.containerType = itemContainerType;
        this.capacity = 288;
        this.tabLimit = 1;
        this.tabs.add(new ItemContainerTab(true));
    }

    public final int getTabCount() {
        return this.tabs.size();
    }

    public final int getTabLimit() {
        return this.tabLimit;
    }

    public final void compactTab(int n) {
        if (n > this.tabLimit - 1) {
            n = this.tabLimit - 1;
        }
        if (n > this.tabs.size() - 1) {
            return;
        }
        ItemContainerTab itemContainerTab = (ItemContainerTab)this.tabs.get(n);
        Iterator iterator = itemContainerTab.items.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = (ItemStack)iterator.next();
            if (itemStack == null) {
                iterator.remove();
                continue;
            }
            if (itemStack.getId() != -1) continue;
            iterator.remove();
        }
    }

    public final void removeEmptyTabs() {
        try {
            Iterator iterator = this.tabs.iterator();
            while (iterator.hasNext()) {
                boolean bl = true;
                Object object3 = (ItemContainerTab)iterator.next();
                if (object3 == null || ((ItemContainerTab)object3).persistent) continue;
                Object object2 = object3;
                if (((ItemContainerTab)object2).items.size() == 0) {
                    iterator.remove();
                    continue;
                }
                object2 = object3;
                for (Object itemObject : ((ItemContainerTab)object2).items) {
                    if (itemObject == null || ((ItemStack)itemObject).getId() == -1) continue;
                    bl = false;
                    break;
                }
                if (!bl) continue;
                iterator.remove();
            }
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    public final int getFirstFreeSlot() {
        int n = 0;
        while (n < this.items.length) {
            if (this.items[n] == null) {
                return n;
            }
            ++n;
        }
        return -1;
    }

    private int getFirstFreeTabSlot(int n) {
        if (n > this.tabLimit - 1) {
            n = this.tabLimit - 1;
        }
        if (n > this.tabs.size() - 1) {
            return 0;
        }
        ItemContainerTab itemContainerTab = (ItemContainerTab)this.tabs.get(n);
        Object object = itemContainerTab;
        object = itemContainerTab;
        object = itemContainerTab.items;
        int n2 = 0;
        while (n2 < ((ArrayList)object).size()) {
            if (((ArrayList)object).get(n2) == null) {
                return n2;
            }
            if (((ItemStack)((ArrayList)object).get(n2)).getId() == -1) {
                return n2;
            }
            ++n2;
        }
        return ((ArrayList)object).size();
    }

    public final ItemStack[] getItems() {
        if (this.tabLimit == 0) {
            return this.items;
        }
        ArrayList arrayList = new ArrayList();
        int n = 0;
        while (n < this.tabs.size()) {
            ItemContainerTab itemContainerTab = (ItemContainerTab)this.tabs.get(n);
            arrayList.addAll(itemContainerTab.items);
            ++n;
        }
        return (ItemStack[])arrayList.toArray(new ItemStack[0]);
    }

    public final boolean addToTab(ItemStack itemStack, int n) {
        if (n > this.tabLimit - 1) {
            n = this.tabLimit - 1;
        }
        if (n > this.tabs.size() - 1) {
            this.tabs.add(new ItemContainerTab('\u0000'));
        }
        return this.addToTabInternal(itemStack, -1, n);
    }

    public final boolean add(ItemStack itemStack, int n) {
        int n2;
        if (itemStack == null) {
            return false;
        }
        int n3 = n2 = n >= 0 ? n : this.getFirstFreeSlot();
        if ((itemStack.getDefinition().isStackable() || this.containerType.equals((Object)ItemContainerType.b)) && !this.containerType.equals((Object)ItemContainerType.c) && this.getItemAmount(itemStack.getId()) > 0 && itemStack.getMetadata() == -1) {
            n2 = this.indexOfItem(itemStack.getId());
        }
        if (n2 == -1) {
            return false;
        }
        if (this.getItemAt(n2) != null) {
            n2 = this.getFirstFreeSlot();
        }
        if ((itemStack.getDefinition().isStackable() || this.containerType.equals((Object)ItemContainerType.b)) && !this.containerType.equals((Object)ItemContainerType.c)) {
            int n4 = 0;
            while (n4 < this.items.length) {
                if (this.items[n4] != null && this.items[n4].getId() == itemStack.getId() && itemStack.getMetadata() == -1) {
                    int n5 = itemStack.getAmount() + this.items[n4].getAmount();
                    if (n5 > Integer.MAX_VALUE || n5 <= 0) {
                        return false;
                    }
                    this.setItem(n4, new ItemStack(this.items[n4].getId(), this.items[n4].getAmount() + itemStack.getAmount(), this.items[n4].getMetadata()));
                    return true;
                }
                ++n4;
            }
            if (n2 == -1) {
                return false;
            }
            this.setItem(n >= 0 ? n2 : this.getFirstFreeSlot(), itemStack);
            return true;
        }
        int n6 = this.getFreeSlots();
        if (n6 >= itemStack.getAmount()) {
            boolean bl = this.updatesEnabled;
            this.updatesEnabled = false;
            try {
                n6 = 0;
                while (n6 < itemStack.getAmount()) {
                    this.setItem(n >= 0 ? n2 : this.getFirstFreeSlot(), new ItemStack(itemStack.getId(), 1, itemStack.getMetadata()));
                    ++n6;
                }
                if (bl) {
                    this.notifyFullRefresh();
                }
                return true;
            }
            finally {
                this.updatesEnabled = bl;
            }
        }
        return false;
    }

    private boolean addToTabInternal(ItemStack itemStack, int n, int n2) {
        if (itemStack == null) {
            return false;
        }
        n = this.getFirstFreeTabSlot(n2);
        if ((itemStack.getDefinition().isStackable() || this.containerType.equals((Object)ItemContainerType.b)) && !this.containerType.equals((Object)ItemContainerType.c) && this.getItemAmount(itemStack.getId()) > 0 && itemStack.getMetadata() == -1) {
            n = this.indexOfItemInTab(itemStack.getId(), n2);
        }
        if (n == -1) {
            return false;
        }
        if (this.getItemAtTabSlot(n, n2) != null) {
            n = this.getFirstFreeTabSlot(n2);
        }
        if ((itemStack.getDefinition().isStackable() || this.containerType.equals((Object)ItemContainerType.b)) && !this.containerType.equals((Object)ItemContainerType.c)) {
            Object object = (ItemContainerTab)this.tabs.get(n2);
            object = ((ItemContainerTab)object).items;
            int n3 = 0;
            while (n3 < ((ArrayList)object).size()) {
                if (((ArrayList)object).get(n3) != null && ((ItemStack)((ArrayList)object).get(n3)).getId() == itemStack.getId() && itemStack.getMetadata() == -1) {
                    n = itemStack.getAmount() + ((ItemStack)((ArrayList)object).get(n3)).getAmount();
                    if (n >= Integer.MAX_VALUE || n <= 0) {
                        return false;
                    }
                    this.setTabItem(n3, new ItemStack(((ItemStack)((ArrayList)object).get(n3)).getId(), ((ItemStack)((ArrayList)object).get(n3)).getAmount() + itemStack.getAmount(), ((ItemStack)((ArrayList)object).get(n3)).getMetadata()), n2);
                    return true;
                }
                ++n3;
            }
            if (n == -1) {
                return false;
            }
            this.setTabItem(this.getFirstFreeTabSlot(n2), itemStack, n2);
            return true;
        }
        int n4 = this.getFreeSlots();
        if (n4 >= itemStack.getAmount()) {
            boolean bl = this.updatesEnabled;
            this.updatesEnabled = false;
            try {
                n = 0;
                while (n < itemStack.getAmount()) {
                    this.setTabItem(this.getFirstFreeSlot(), new ItemStack(itemStack.getId(), 1, itemStack.getMetadata()), n2);
                    ++n;
                }
                if (bl) {
                    this.notifyFullRefresh();
                }
                return true;
            }
            finally {
                this.updatesEnabled = bl;
            }
        }
        return false;
    }

    public final int getFreeSlots() {
        return this.capacity - this.getUsedSlots();
    }

    public final ItemStack getItemAt(int n) {
        if (n == -1) {
            return null;
        }
        return this.items[n];
    }

    public final ItemStack getItemAtTabSlot(int n, int n2) {
        if (n2 > this.tabLimit - 1) {
            n2 = this.tabLimit - 1;
        }
        if (n2 > this.tabs.size() - 1) {
            return null;
        }
        ItemContainerTab itemContainerTab = (ItemContainerTab)this.tabs.get(n2);
        if (n > itemContainerTab.items.size() - 1) {
            return null;
        }
        itemContainerTab = (ItemContainerTab)this.tabs.get(n2);
        return (ItemStack)itemContainerTab.items.get(n);
    }

    public final ItemStack findFlatItem(int n) {
        ItemStack[] itemStackArray = this.items;
        int n2 = this.items.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemStack = itemStackArray[n3];
            if (itemStack != null && itemStack.getId() == n) {
                return itemStack;
            }
            ++n3;
        }
        return null;
    }

    public final int indexOfItem(int n) {
        int n2 = 0;
        while (n2 < this.items.length) {
            if (this.items[n2] != null && this.items[n2].getId() == n) {
                return n2;
            }
            ++n2;
        }
        return -1;
    }

    public final int indexOfItemInTab(int n, int n2) {
        if (n2 > this.tabLimit - 1) {
            n2 = this.tabLimit - 1;
        }
        if (n2 > this.tabs.size() - 1) {
            return -1;
        }
        Object object = (ItemContainerTab)this.tabs.get(n2);
        object = ((ItemContainerTab)object).items;
        int n3 = 0;
        while (n3 < ((ArrayList)object).size()) {
            if (((ArrayList)object).get(n3) != null && ((ItemStack)((ArrayList)object).get(n3)).getId() == n) {
                return n3;
            }
            ++n3;
        }
        return -1;
    }

    public final int indexOfPlaceholderInTab(int n, int n2, int n3) {
        if (n3 > this.tabLimit - 1) {
            n3 = this.tabLimit - 1;
        }
        if (n3 > this.tabs.size() - 1) {
            return -1;
        }
        Object object = (ItemContainerTab)this.tabs.get(n3);
        object = ((ItemContainerTab)object).items;
        n3 = 0;
        while (n3 < ((ArrayList)object).size()) {
            if (((ArrayList)object).get(n3) != null && ((ItemStack)((ArrayList)object).get(n3)).getId() == n && ((ItemStack)((ArrayList)object).get(n3)).getAmount() == 0) {
                return n3;
            }
            ++n3;
        }
        return -1;
    }

    public final void setItem(int n, ItemStack itemStack) {
        this.items[n] = itemStack;
        if (this.updatesEnabled) {
            this.notifySlotUpdated(n);
        }
    }

    public final void setTabItem(int n, ItemStack itemStack, int n2) {
        if (n2 > this.tabLimit - 1) {
            n2 = this.tabLimit - 1;
        }
        if (n2 > this.tabs.size() - 1) {
            this.tabs.add(new ItemContainerTab('\u0000'));
        }
        ItemContainerTab itemContainerTab = (ItemContainerTab)this.tabs.get(n2);
        if (n > itemContainerTab.items.size() - 1) {
            itemContainerTab = (ItemContainerTab)this.tabs.get(n2);
            itemContainerTab.items.add(itemStack);
        } else {
            itemContainerTab = (ItemContainerTab)this.tabs.get(n2);
            itemContainerTab.items.set(n, itemStack);
        }
        if (this.updatesEnabled) {
            this.notifySlotUpdated(n);
        }
    }

    public final void replaceItemId(int n, int n2) {
        ItemStack itemStack = new ItemStack(n);
        ItemContainer itemContainer = this;
        itemContainer.removeInternal(itemStack, -1, false);
        itemStack = new ItemStack(n2);
        itemContainer = this;
        itemContainer.add(itemStack, -1);
    }

    public final int g() {
        return this.capacity;
    }

    public final int getUsedSlots() {
        int n = 0;
        if (this.tabLimit == 0) {
            ItemStack[] itemStackArray = this.items;
            int n2 = this.items.length;
            int n3 = 0;
            while (n3 < n2) {
                ItemStack itemStack = itemStackArray[n3];
                if (itemStack != null) {
                    ++n;
                }
                ++n3;
            }
        } else {
            for (Object itemContainerTabObject : this.tabs) {
                ItemContainerTab itemContainerTab = (ItemContainerTab)itemContainerTabObject;
                if (itemContainerTab == null) continue;
                for (Object itemStackObject : itemContainerTab.items) {
                    ItemStack itemStack = (ItemStack)itemStackObject;
                    if (itemStack == null || itemStack.getId() == -1) continue;
                    ++n;
                }
            }
            return n;
        }
        return n;
    }

    public void clear() {
        if (this.tabLimit == 0) {
            this.items = new ItemStack[this.items.length];
            if (this.updatesEnabled) {
                this.notifyFullRefresh();
                return;
            }
        } else {
            for (Object itemContainerTabObject : this.tabs) {
                ItemContainerTab itemContainerTab = (ItemContainerTab)itemContainerTabObject;
                if (itemContainerTab == null) continue;
                itemContainerTab.items.clear();
            }
        }
    }

    public final ItemStack[] getRawItems() {
        return this.items;
    }

    public final ItemStack[] getTabItems(int n) {
        ItemStack[] itemStackArray = new ItemStack[288];
        if (n > this.tabLimit - 1) {
            n = this.tabLimit - 1;
        }
        if (n > this.tabs.size() - 1) {
            return itemStackArray;
        }
        Object object = (ItemContainerTab)this.tabs.get(n);
        object = ((ItemContainerTab)object).items;
        int n2 = 0;
        while (n2 < ((ArrayList)object).size()) {
            itemStackArray[n2] = (ItemStack)((ArrayList)object).get(n2);
            ++n2;
        }
        return itemStackArray;
    }

    public final boolean hasItemAtSlot(int n) {
        return this.items[n] != null;
    }

    public final int remove(ItemStack itemStack) {
        return this.removeInternal(itemStack, -1, false);
    }

    public final int removeKeepingPlaceholder(ItemStack itemStack) {
        return this.removeInternal(itemStack, -1, true);
    }

    public final int removeFromSlot(ItemStack itemStack, int n) {
        return this.removeInternal(itemStack, n, false);
    }

    public final int removeFromTab(ItemStack itemStack, int n, int n2) {
        int n3 = n2;
        boolean bl = false;
        n2 = n;
        ItemStack itemStack2 = itemStack;
        ItemContainer itemContainer = this;
        if (itemStack2 == null) {
            return -1;
        }
        int n4 = 0;
        if ((itemStack2.getDefinition().isStackable() || itemContainer.containerType.equals((Object)ItemContainerType.b)) && !itemContainer.containerType.equals((Object)ItemContainerType.c)) {
            ItemStack itemStack3;
            int n5 = itemContainer.indexOfItemInTab(itemStack2.getId(), n3);
            if (itemStack2.getMetadata() != -1) {
                n5 = n2;
            }
            if ((itemStack3 = itemContainer.getItemAtTabSlot(n5, n3)) == null) {
                return -1;
            }
            if (itemStack3.getAmount() > itemStack2.getAmount()) {
                n4 = itemStack2.getAmount();
                itemContainer.setTabItem(n5, new ItemStack(itemStack3.getId(), itemStack3.getAmount() - itemStack2.getAmount(), itemStack3.getMetadata()), n3);
            } else {
                n4 = itemStack3.getAmount();
                itemContainer.setTabItem(n5, null, n3);
            }
        } else {
            int n6 = 0;
            while (n6 < itemStack2.getAmount()) {
                ItemStack itemStack4;
                int n7 = itemContainer.indexOfItemInTab(itemStack2.getId(), n3);
                if (n6 == 0 && n2 != -1 && (itemStack4 = itemContainer.getItemAtTabSlot(n2, n3)).getId() == itemStack2.getId()) {
                    n7 = n2;
                }
                if (n7 == -1) break;
                ++n4;
                itemContainer.setTabItem(n7, null, n3);
                ++n6;
            }
        }
        return n4;
    }

    private int removeInternal(ItemStack itemStack, int n, boolean bl) {
        if (itemStack == null) {
            return -1;
        }
        int n2 = 0;
        if ((itemStack.getDefinition().isStackable() || this.containerType.equals((Object)ItemContainerType.b)) && !this.containerType.equals((Object)ItemContainerType.c)) {
            ItemStack itemStack2;
            int n3 = this.indexOfItem(itemStack.getId());
            if (itemStack.getMetadata() != -1) {
                n3 = n;
            }
            if ((itemStack2 = this.getItemAt(n3)) == null) {
                return -1;
            }
            if (itemStack2.getAmount() > itemStack.getAmount()) {
                n2 = itemStack.getAmount();
                this.setItem(n3, new ItemStack(itemStack2.getId(), itemStack2.getAmount() - itemStack.getAmount(), itemStack2.getMetadata()));
            } else {
                n2 = itemStack2.getAmount();
                this.setItem(n3, bl ? new ItemStack(itemStack2.getId(), 0, itemStack2.getMetadata()) : null);
            }
        } else {
            int n4 = 0;
            while (n4 < itemStack.getAmount()) {
                ItemStack itemStack3;
                int n5 = this.indexOfItem(itemStack.getId());
                if (n4 == 0 && n != -1 && (itemStack3 = this.getItemAt(n)).getId() == itemStack.getId()) {
                    n5 = n;
                }
                if (n5 == -1) break;
                ++n2;
                this.setItem(n5, null);
                ++n4;
            }
        }
        return n2;
    }

    public final void swapSlots(int n, int n2) {
        ItemStack itemStack = this.getItemAt(n);
        boolean bl = this.updatesEnabled;
        this.updatesEnabled = false;
        try {
            if (n2 > this.items.length - 1) {
                n2 = this.items.length - 1;
            }
            this.setItem(n, this.getItemAt(n2));
            this.setItem(n2, itemStack);
            if (bl) {
                this.notifySlotsUpdated(new int[]{n, n2});
            }
        }
        finally {
            this.updatesEnabled = bl;
        }
    }

    public final void swapTabSlots(int n, int n2, int n3, int n4) {
        ItemStack itemStack = this.getItemAtTabSlot(n, n3);
        boolean bl = this.updatesEnabled;
        this.updatesEnabled = false;
        try {
            this.setTabItem(n, this.getItemAtTabSlot(n2, n4), n3);
            this.setTabItem(n2, itemStack, n4);
            if (bl) {
                this.notifySlotsUpdated(new int[]{n, n2});
            }
        }
        finally {
            this.updatesEnabled = bl;
        }
    }

    public final int getItemAmount(int n) {
        int n2 = 0;
        if (this.tabLimit == 0) {
            ItemStack[] itemStackArray = this.items;
            int n3 = this.items.length;
            int n4 = 0;
            while (n4 < n3) {
                ItemStack itemStack = itemStackArray[n4];
                if (itemStack != null && itemStack.getId() == n) {
                    n2 += itemStack.getAmount();
                }
                ++n4;
            }
        } else {
            for (Object itemContainerTabObject : this.tabs) {
                ItemContainerTab itemContainerTab = (ItemContainerTab)itemContainerTabObject;
                if (itemContainerTab == null) continue;
                for (Object itemStackObject : itemContainerTab.items) {
                    ItemStack itemStack = (ItemStack)itemStackObject;
                    if (itemStack == null || itemStack.getId() != n) continue;
                    n2 += itemStack.getAmount();
                }
            }
        }
        return n2;
    }

    public final ItemStack findItem(int n) {
        if (this.tabLimit == 0) {
            ItemStack[] itemStackArray = this.items;
            int n2 = this.items.length;
            int n3 = 0;
            while (n3 < n2) {
                ItemStack itemStack = itemStackArray[n3];
                if (itemStack != null && itemStack.getId() == n) {
                    return itemStack;
                }
                ++n3;
            }
        } else {
            for (Object itemContainerTabObject : this.tabs) {
                ItemContainerTab itemContainerTab = (ItemContainerTab)itemContainerTabObject;
                if (itemContainerTab == null) continue;
                for (Object itemStackObject : itemContainerTab.items) {
                    ItemStack itemStack = (ItemStack)itemStackObject;
                    if (itemStack == null || itemStack.getId() != n) continue;
                    return itemStack;
                }
            }
        }
        return null;
    }

    public final int findTabContainingPlaceholder(int n, int n2) {
        n2 = 0;
        while (n2 < this.tabs.size()) {
            Object object = (ItemContainerTab)this.tabs.get(n2);
            object = ((ItemContainerTab)object).items;
            Iterator iterator = ((ArrayList)object).iterator();
            while (iterator.hasNext()) {
                object = (ItemStack)iterator.next();
                if (object == null || ((ItemStack)object).getId() != n || ((ItemStack)object).getAmount() != 0) continue;
                return n2;
            }
            ++n2;
        }
        return -1;
    }

    public final int findTabContainingItem(int n) {
        int n2 = 0;
        while (n2 < this.tabs.size()) {
            Object object = (ItemContainerTab)this.tabs.get(n2);
            object = ((ItemContainerTab)object).items;
            Iterator iterator = ((ArrayList)object).iterator();
            while (iterator.hasNext()) {
                object = (ItemStack)iterator.next();
                if (object == null || ((ItemStack)object).getId() != n) continue;
                return n2;
            }
            ++n2;
        }
        return -1;
    }

    public final ItemContainerTab getTab(int n) {
        if (n > this.tabLimit - 1) {
            n = this.tabLimit - 1;
        }
        if (n > this.tabs.size() - 1) {
            return null;
        }
        return (ItemContainerTab)this.tabs.get(n);
    }

    public final void moveTabItem(int n, int n2, int n3) {
        if (n3 > this.tabLimit - 1) {
            n3 = this.tabLimit - 1;
        }
        if (n3 > this.tabs.size() - 1) {
            return;
        }
        Object object = (ItemContainerTab)this.tabs.get(n3);
        object = ((ItemContainerTab)object).items;
        ItemStack itemStack = (ItemStack)((ArrayList)object).get(n);
        if (itemStack == null) {
            return;
        }
        if (n2 > n) {
            while (n < n2) {
                itemStack = (ItemStack)((ArrayList)object).get(n);
                ((ArrayList)object).set(n, (ItemStack)((ArrayList)object).get(n + 1));
                ((ArrayList)object).set(n + 1, itemStack);
                ++n;
            }
        } else if (n > n2) {
            while (n > n2) {
                itemStack = (ItemStack)((ArrayList)object).get(n);
                ((ArrayList)object).set(n, (ItemStack)((ArrayList)object).get(n - 1));
                ((ArrayList)object).set(n - 1, itemStack);
                --n;
            }
        }
        if (this.updatesEnabled) {
            this.notifyFullRefresh();
        }
    }

    private void notifySlotUpdated(int n) {
        Iterator iterator = this.updateListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    private void notifyFullRefresh() {
        Iterator iterator = this.updateListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    private void notifySlotsUpdated(int[] object) {
        Iterator iterator = this.updateListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    public final boolean containsItem(int n) {
        return this.indexOfItem(n) != -1;
    }

    public final boolean canAdd(ItemStack itemStack) {
        if ((itemStack.getDefinition().isStackable() || this.containerType.equals((Object)ItemContainerType.b)) && !this.containerType.equals((Object)ItemContainerType.c)) {
            ItemStack[] itemStackArray = this.items;
            int n = this.items.length;
            int n2 = 0;
            while (n2 < n) {
                ItemStack itemStack2 = itemStackArray[n2];
                if (itemStack2 != null && itemStack2.getId() == itemStack.getId()) {
                    long l;
                    long l2 = itemStack.getAmount();
                    long l3 = l2 + (l = (long)itemStack2.getAmount());
                    return l3 <= Integer.MAX_VALUE && l3 >= 1L;
                }
                ++n2;
            }
            int n3 = this.getFirstFreeSlot();
            return n3 != -1;
        }
        int n = this.getFreeSlots();
        return n >= itemStack.getAmount();
    }

    public final int k() {
        return this.capacity;
    }
}

