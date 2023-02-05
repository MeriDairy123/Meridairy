package b2infosoft.milkapp.com.Navigation;

import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * Created by Choudhary on 02/01/2019.
 */
public class NavDrawerItem {

    List<NavInnerItem> subMenu;
    private boolean showNotify;
    private String title;
    private int thumbnail;
    private Fragment fragment;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title, int thumbnail, Fragment fragment, List<NavInnerItem> subMenu) {
        this.showNotify = showNotify;
        this.title = title;
        this.thumbnail = thumbnail;
        this.fragment = fragment;
        this.subMenu = subMenu;
    }


    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public List<NavInnerItem> getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(List<NavInnerItem> subMenu) {
        this.subMenu = subMenu;
    }

}
