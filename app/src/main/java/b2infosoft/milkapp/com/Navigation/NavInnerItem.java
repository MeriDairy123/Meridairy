package b2infosoft.milkapp.com.Navigation;

import androidx.fragment.app.Fragment;

/**
 * Created by Choudhary on 6/28/2017.
 */
public class NavInnerItem {

    private String title;
    private int thumbnail;
    private Fragment fragment;

    NavInnerItem(String title, int thumbnail, Fragment fragment) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.fragment = fragment;
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
}
