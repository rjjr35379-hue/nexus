package rj.nexus.systems.bedrock.model.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class BoneItem {
    @SerializedName("name")
    private String name;
    @SerializedName("parent")
    private String parent;
    @SerializedName("pivot")
    private float[] pivot;
    @SerializedName("rotation")
    private float[] rotation;
    @SerializedName("mirror")
    private boolean mirror;
    @SerializedName("cubes")
    private CubeItem[] cubes;
    @SerializedName("locators")
    private Map<String, LocatorItem> locators;

    public String getName()              { return name; }
    public String getParent()            { return parent; }
    public float[] getPivot()            { return pivot; }
    public float[] getRotation()         { return rotation; }
    public boolean isMirror()            { return mirror; }
    public CubeItem[] getCubes()         { return cubes; }
    public Map<String, LocatorItem> getLocators() { return locators; }
}