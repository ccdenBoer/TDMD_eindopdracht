package com.example.mobile_development_2_2.gui.fragments.home

import android.util.Log
import com.example.mobile_development_2_2.R
import com.example.mobile_development_2_2.data.Lang

class HelpItem(title: String, imgId: Int, description: String) {
    val title = title
    val imgId = imgId
    val description = description


    companion object{
        fun getItems(): List<HelpItem> {
            var item1 = HelpItem(Lang.get(R.string.HelpItem1), R.drawable.img_inforoute, Lang.get(R.string.HelpItem1Description))
            var item2 = HelpItem(Lang.get(R.string.HelpItem2), R.drawable.img_infomap, Lang.get(R.string.HelpItem2Description))
            var item3 = HelpItem(Lang.get(R.string.HelpItem3), R.drawable.img_infopause, Lang.get(R.string.HelpItem3Description))
            var item4 = HelpItem(Lang.get(R.string.HelpItem4), R.drawable.img_infopoi, Lang.get(R.string.HelpItem4Description))
            var item5 = HelpItem(Lang.get(R.string.HelpItem5), R.drawable.img_infopoimap, Lang.get(R.string.HelpItem5Description))
            var item6 = HelpItem(Lang.get(R.string.HelpItem6), R.drawable.img_infopoilist, Lang.get(R.string.HelpItem6Description))
            var item7 = HelpItem(Lang.get(R.string.HelpItem7), R.drawable.img_infolang, Lang.get(R.string.HelpItem7Description))
            var item8 = HelpItem(Lang.get(R.string.HelpItem8), R.drawable.img_infocolor, Lang.get(R.string.HelpItem8Description))



            return listOf(item1, item2, item3, item4, item5, item6, item7, item8)
        }

        var selectedItem = HelpItem("should be null", R.drawable.ic_launcher_background, "how")

        fun selectItem(item: HelpItem){
            Log.d("a", "Item selected")
            selectedItem = item
        }

        @JvmName("getSelectedItem1")
        fun getSelectedItem(): HelpItem{
            return selectedItem
        }
    }
}
