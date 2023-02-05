package b2infosoft.milkapp.com.Model;

/**
 * Created by Choudhary Computer on 7/21/2018.
 */

public class Help_item {

    private String video_title = "";
    private String video_link = "";


    public Help_item(String videoTitle, String video_link) {

        this.video_title = videoTitle;
        this.video_link = video_link;

    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }


}
