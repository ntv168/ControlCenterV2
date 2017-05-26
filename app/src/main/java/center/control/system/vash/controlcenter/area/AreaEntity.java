package center.control.system.vash.controlcenter.area;

import android.graphics.Bitmap;

/**
 * Created by Thuans on 5/26/2017.
 */

public class AreaEntity {
    public  static final String[] attrivutes = {"An ninh","Ánh sáng","Nhiệt độ","Âm thanh", "Thiết bị sử dụng điện"};
    private int id;
    private String connectAddress;
    private String name;
    private Bitmap imageBitmap;

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConnectAddress() {
        return connectAddress;
    }

    public void setConnectAddress(String connectAddress) {
        this.connectAddress = connectAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
