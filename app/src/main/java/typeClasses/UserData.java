package typeClasses;

public class UserData {
     /*
            {"name":"aloo name","username":"aloo","email":"aloo@","homeNumber":"","phoneNumber":"","address":"alooAdr","nif":"","cc":""}
             */
     String name;
     String username;
     String email;
     String homeNumber;
     String phoneNumber;
     String address;
     String nif;
     String cc;



    public UserData(String name, String username, String email, String homeNumber,
                    String phoneNumber, String address, String nif, String cc) {
         this.name = name;
         this.username = username;
         this.email = email;
         this.homeNumber = homeNumber;
         this.phoneNumber = phoneNumber;
         this.address = address;
         this.nif = nif;
         this.cc = cc;
     }

     public String[] getUserInfo() {
         String[] userInfo = new String[8];
         userInfo[0] = name;
         userInfo[1] = username;
         userInfo[2] = email;
         userInfo[3] = homeNumber;
         userInfo[4] = phoneNumber;
         userInfo[5] = address;
         userInfo[6] = nif;
         userInfo[7] = cc;
         return userInfo;
     }

    public String getAddress() {
        return address;
    }

    public String getCc() {
        return cc;
    }
    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNif() {
        return nif;
    }
}
