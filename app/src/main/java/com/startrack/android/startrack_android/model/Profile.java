package com.startrack.android.startrack_android.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vrogovskiy on 2/14/16.
 */
public class Profile {
    private String email = "";
    private String profilePic = "";
    private int socNetworkType;
    private int userId;
    //private List<EditableProfileProperty> editableProperties;
    //private List<SelectableProfileProperty> selectableProperties;
    public Map<Integer, EditableProfileProperty> editableProperties = new HashMap<Integer,EditableProfileProperty>();
    public Map<Integer, SelectableProfileProperty> selectableProperties = new HashMap<Integer,SelectableProfileProperty>();


    public Profile(JSONObject jObject) throws JSONException {
        if(jObject.has("email")) {
            if(!jObject.getString("email").equals("")&&!jObject.getString("email").equals("null")) {
                this.email = jObject.getString("email");
            }
            this.email = "";
        } else {
            this.email = "";
        }
        if(jObject.has("user_id")) {
            if(!jObject.isNull("user_id")) {
                this.userId = jObject.getInt("user_id");
            }
        } else {
            this.userId = (-1);
        }
        if(jObject.has("soc_network_type")) {
            if(!jObject.isNull("soc_network_type")) {
                this.socNetworkType = jObject.getInt("soc_network_type");
            }
        } else {
            this.socNetworkType = (-1);
        }
        if(jObject.has("profile_pic")) {
            if(!jObject.getString("profile_pic").equals("")&&!jObject.getString("profile_pic").equals("null")) {
                this.profilePic = jObject.getString("profile_pic");
            }
        } else {
            this.profilePic = "";
        }
        initPropertiesArray();
        if(jObject.has("first_name")) {
            if(!jObject.getString("first_name").equals("")&&!jObject.getString("first_name").equals("null")) {
                this.editableProperties.get(0).setPropertyValue(jObject.getString("first_name"));
            } else {
                this.editableProperties.get(0).setPropertyValue("");
            }
        } else {
            this.editableProperties.get(0).setPropertyValue("");
        }
        if(jObject.has("last_name")) {
            if(!jObject.getString("last_name").equals("")&&!jObject.getString("last_name").equals("null")) {
                this.editableProperties.get(1).setPropertyValue(jObject.getString("last_name"));
            } else {
                this.editableProperties.get(1).setPropertyValue("");
            }
        } else {
            this.editableProperties.get(1).setPropertyValue("");
        }
        if(jObject.has("phone_number")) {
            if(!jObject.getString("phone_number").equals("")&&!jObject.getString("phone_number").equals("null")) {
                this.editableProperties.get(2).setPropertyValue(jObject.getString("phone_number"));
            } else {
                this.editableProperties.get(2).setPropertyValue("");
            }
        } else {
            this.editableProperties.get(2).setPropertyValue("");
        }
        if(jObject.has("company_name")) {
            if(!jObject.getString("company_name").equals("")&&!jObject.getString("company_name").equals("null")) {
                this.editableProperties.get(3).setPropertyValue(jObject.getString("company_name"));
            } else {
                this.editableProperties.get(3).setPropertyValue("");
            }
        } else {
            this.editableProperties.get(3).setPropertyValue("");
        }

        if(jObject.has("size_id")) {
            if(!jObject.isNull("size_id")) {
                this.selectableProperties.get(4).setValueId(jObject.getInt("size_id"));
            } else {
                this.selectableProperties.get(4).setValueId(-1);
            }
        } else {
            this.selectableProperties.get(4).setValueId(-1);
        }

        if(jObject.has("title_id")) {
            if(!jObject.isNull("title_id")) {
                this.selectableProperties.get(5).setValueId(jObject.getInt("title_id"));
            } else {
                this.selectableProperties.get(5).setValueId(-1);
            }
        } else {
            this.selectableProperties.get(5).setValueId(-1);
        }
        if(jObject.has("seniority_id")) {
            if(!jObject.isNull("seniority_id")) {
                this.selectableProperties.get(6).setValueId(jObject.getInt("seniority_id"));
            }else {
                this.selectableProperties.get(6).setValueId(-1);
            }
        } else {
            this.selectableProperties.get(6).setValueId(-1);
        }

        if(jObject.has("industry_id")) {
            if(!jObject.isNull("industry_id")) {
                this.selectableProperties.get(7).setValueId(jObject.getInt("industry_id"));
            }else {
                this.selectableProperties.get(7).setValueId(-1);
            }
        } else {
            this.selectableProperties.get(7).setValueId(-1);
        }

        if(jObject.has("country_id")) {
            if(!jObject.isNull("country_id")) {
                this.selectableProperties.get(8).setValueId(jObject.getInt("country_id"));
            }  else {
                    this.selectableProperties.get(8).setValueId(-1);
            }
        }else {
            this.selectableProperties.get(8).setValueId(-1);
        }

        if(jObject.has("state_id")) {
            if(!jObject.isNull("state_id")) {
                this.selectableProperties.get(9).setValueId(jObject.getInt("state_id"));
            } else {
                this.selectableProperties.get(9).setValueId(-1);
            }
        } else {
            this.selectableProperties.get(9).setValueId(-1);
        }

        if(jObject.has("position_id")) {
            if(!jObject.isNull("position_id")) {
                this.selectableProperties.get(10).setValueId(jObject.getInt("position_id"));
            } else {
                this.selectableProperties.get(10).setValueId(-1);
            }
        } else {
            this.selectableProperties.get(10).setValueId(-1);
        }

        if(jObject.has("city_name")) {
            if(!jObject.getString("city_name").equals("")&&!jObject.getString("city_name").equals("null")) {
                this.editableProperties.get(11).setPropertyValue(jObject.getString("city_name"));
            } else {
                this.editableProperties.get(11).setPropertyValue("");
            }
        } else {
            this.editableProperties.get(11).setPropertyValue("");
        }
    }

 /*  public Profile(String email){
        this.email = email;
        initPropertiesArray();
        //initWIthDummyData();
    }*/

     public Profile(){
        initPropertiesArray();
        //initWIthDummyData();
    }

    private void initPropertiesArray(){
        for(int i=0; i<4; i++){
            this.editableProperties.put(i, new EditableProfileProperty(i));
        }
        this.editableProperties.put(11, new EditableProfileProperty(11));
        for(int i=4; i<11; i++){
            this.selectableProperties.put(i, new SelectableProfileProperty(i));
        }
    }

/*    public void initWIthDummyData(){
        *//*    0 - first name
*     1 - last name
*     2 - phone number
*     3 - company name
*     4 - company size
*     5 - title
*     6 - seniority
*     7 - industry
*     8 - country
*     9 - state
*     10 - position
*     11 - city
* *//*
        this.editableProperties.add(0, new EditableProfileProperty(0));
        this.editableProperties.add(1, new EditableProfileProperty(1));
        this.editableProperties.add(2, new EditableProfileProperty(2));
        this.editableProperties.add(3, new EditableProfileProperty(3));

        this.selectableProperties.add(0, new SelectableProfileProperty(4));
        this.selectableProperties.add(1, new SelectableProfileProperty(5));
        this.selectableProperties.add(2, new SelectableProfileProperty(6));
        this.selectableProperties.add(3, new SelectableProfileProperty(7));
        this.selectableProperties.add(4, new SelectableProfileProperty(8));
        this.selectableProperties.add(5, new SelectableProfileProperty(9));
        this.selectableProperties.add(6, new SelectableProfileProperty(10));

        SelectableProfileProperty.writeDummyData();
    }*/

/*    private void init(){

    }*/

    public String getEmail() {
        return email;
    }

    public void setEmail(String em) {
        email = em;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getSocNetworkType() {
        return socNetworkType;
    }

    public void setSocNetworkType(int socNetworkType) {
        this.socNetworkType = socNetworkType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
