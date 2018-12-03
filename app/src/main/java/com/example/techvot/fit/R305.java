package com.example.techvot.fit;

//import static com.example.techvot.fit.Transaction.usbManager;

import android.widget.TextView;

public class R305 {//extends splash


    private UsbService usbService ;

    public static int thePassword = 0, theAddress = 0xFFFFFFFF;

    public static int
        FINGERPRINT_OK = 0x00 ,
        FINGERPRINT_PACKETRECIEVEERR = 0x01 ,
        FINGERPRINT_NOFINGER = 0x02,
        FINGERPRINT_IMAGEFAIL = 0x03,
       FINGERPRINT_TOO_DRY         = 0x04,
       FINGERPRINT_TOO_WET         = 0x05,
        FINGERPRINT_IMAGEMESS = 0x06,
        FINGERPRINT_FEATUREFAIL = 0x07,
        FINGERPRINT_NOMATCH  = 0x08,
        FINGERPRINT_NOTFOUND = 0x09,
        FINGERPRINT_ENROLLMISMATCH = 0x0A,
        FINGERPRINT_BADLOCATION = 0x0B,
        FINGERPRINT_DBRANGEFAIL = 0x0C,
        FINGERPRINT_UPLOADFEATUREFAIL = 0x0D,
        FINGERPRINT_PACKETRESPONSEFAIL = 0x0E,
        FINGERPRINT_UPLOADFAIL = 0x0F,
        FINGERPRINT_DELETEFAIL = 0x10,
        FINGERPRINT_DBCLEARFAIL = 0x11,
       FINGERPRINT_SLEEP_ERR          = 0x12,
        FINGERPRINT_PASSFAIL = 0x13,
       FINGERPRINT_RESET_ERR          = 0x14,
        FINGERPRINT_INVALIDIMAGE = 0x15,
       FINGERPRINT_HANGOVER_UNREMOVE  = 0X17,
        FINGERPRINT_FLASHERR = 0x18,
        FINGERPRINT_INVALIDREG = 0x1A,
        FINGERPRINT_VERIFYPASSWORD = 0x13,
        FINGERPRINT_ADDRCODE = 0x20,
        FINGERPRINT_PASSVERIFY = 0x21,
        FINGERPRINT_STARTCODE = 0xEF01,
        FINGERPRINT_COMMANDPACKET = 0x1,
        FINGERPRINT_DATAPACKET = 0x2,
        FINGERPRINT_ACKPACKET = 0x7,
        FINGERPRINT_ENDDATAPACKET = 0x8,
        FINGERPRINT_TIMEOUT = 0xFF,
        FINGERPRINT_BADPACKET = 0xFE,
        FINGERPRINT_GETIMAGE = 0x01,
        FINGERPRINT_IMAGE2TZ = 0x02,
        FINGERPRINT_REGMODEL = 0x05,
        FINGERPRINT_STORE = 0x06,
        FINGERPRINT_LOAD = 0x07,
        FINGERPRINT_UPLOAD = 0x08,
        FINGERPRINT_CAPTURE = 0x09,
        FINGERPRINT_DELETE = 0x0C,
        FINGERPRINT_EMPTY = 0x0D,
        FINGERPRINT_SETPASSWORD = 0x12,
        FINGERPRINT_HISPEEDSEARCH = 0x1B,
        FINGERPRINT_TEMPLATECOUNT = 0x1D,
        FINGERPRINT_BUFFER = 0x0A,
        DEFAULTTIMEOUT = 1000 ; ///< UART reading timeout in milliseconds


//    public R305(){
//
//
//    }

    public void PortContrl() {
        if (usbService != null) {

            int[] packet = {0x17, R305.thePassword, 0, 0, 0};

            writePacket(R305.theAddress, R305.FINGERPRINT_COMMANDPACKET, 4, packet);// FINGERPRINT_COMMANDPACKET is indentifier

            int idx = 0;
            int[] freply = new int[100];

        }
    }


    ////////////fingerprint
    public void verifyPassword() {
        if (usbService != null) {

            int[] packet = {R305.FINGERPRINT_VERIFYPASSWORD, R305.thePassword, 0, 0, 0};

            writePacket(R305.theAddress, R305.FINGERPRINT_COMMANDPACKET, 7, packet);// FINGERPRINT_COMMANDPACKET is indentifier

            int idx = 0;
            int[] freply = new int[100];

        }
    }


    public byte GenImg() {
        if (usbService != null)
        {
            int[] packet = {R305.FINGERPRINT_GETIMAGE};
            writePacket(R305.theAddress, R305.FINGERPRINT_COMMANDPACKET, 3, packet);//
          /*  int idx = 0;
            int[] freply = new int[100];

            while (usbService.read(mCallback) < 1) ;
            while (usbService.read(mCallback) > 0) {
                //serialPort1.ReadTimeout != 0 ||
                freply[idx] = usbService.read(mCallback);
                idx++;
            }
            return 0;*/
        }

        return 0 ;
    }


    public void writePacket(int Addr, int PacketCMD,int LEN, int[]packet){

        byte [] sc={((byte)((R305.FINGERPRINT_STARTCODE>>8)&0xff)),(byte)(R305.FINGERPRINT_STARTCODE & 0xff)};
        byte [] address={(byte)((Addr>>24) &0xff),(byte)((Addr>>16) &0xff),(byte)((Addr>>8) &0xff),(byte)(Addr&0xff)};//
        byte [] packetType ={(byte)(PacketCMD & 0xFF) };//
        byte [] len ={(byte)((LEN>>8) &0xff),(byte)(LEN &0xff)};//
        usbService.write(sc);
        usbService.write(address);
        usbService.write(packetType);
        usbService.write(len);
        int sum = PacketCMD;
        sum += LEN;
        for(int i= 0; i<LEN-2; i++){
            //dat8 = (int)packet[i];
            byte []sca = {(byte)packet[i]};    //sc = BitConverter.GetBytes(dat8);
            usbService.write(sca);             //serialPort1.write(sc,0,1);
            sum += (int)packet[i];             //sum += (ushort)packet[i];
        }

        byte []arr = {(byte)((sum>> 8) & 0xff),(byte)(sum & 0xff)};
        usbService.write(arr);
    }







}
