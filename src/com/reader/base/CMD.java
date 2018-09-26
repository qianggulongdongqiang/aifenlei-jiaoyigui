package com.reader.base;

public class CMD {
    public final static byte RESET = 0x70;
    public final static byte SET_UART_BAUDRATE = 0x71;
    public final static byte GET_FIRMWARE_VERSION = 0x72;
    public final static byte SET_READER_ADDRESS = 0x73;
    public final static byte SET_WORK_ANTENNA = 0x74;
    public final static byte GET_WORK_ANTENNA = 0x75;
    public final static byte SET_OUTPUT_POWER = 0x76;
    public final static byte GET_OUTPUT_POWER = 0x77;
    public final static byte SET_FREQUENCY_REGION = 0x78;
    public final static byte GET_FREQUENCY_REGION = 0x79;
    public final static byte SET_BEEPER_MODE = 0x7A;
    public final static byte GET_READER_TEMPERATURE = 0x7B;
    public final static byte READ_GPIO_VALUE = 0x60;
    public final static byte WRITE_GPIO_VALUE = 0x61;
    public final static byte SET_ANT_CONNECTION_DETECTOR = 0x62;
    public final static byte GET_ANT_CONNECTION_DETECTOR = 0x63;
    public final static byte SET_TEMPORARY_OUTPUT_POWER = 0x66;
    public final static byte SET_READER_IDENTIFIER = 0x67;
    public final static byte GET_READER_IDENTIFIER = 0x68;
    public final static byte SET_RF_LINK_PROFILE = 0x69;
    public final static byte GET_RF_LINK_PROFILE = 0x6A;
    public final static byte GET_RF_PORT_RETURN_LOSS = 0x7E;
    public final static byte INVENTORY = (byte) 0x80;
    public final static byte READ_TAG = (byte) 0x81;
    public final static byte WRITE_TAG = (byte) 0x82;
    public final static byte LOCK_TAG = (byte) 0x83;
    public final static byte KILL_TAG = (byte) 0x84;
    public final static byte SET_ACCESS_EPC_MATCH = (byte) 0x85;
    public final static byte GET_ACCESS_EPC_MATCH = (byte) 0x86;
    public final static byte REAL_TIME_INVENTORY = (byte) 0x89;
    public final static byte FAST_SWITCH_ANT_INVENTORY = (byte) 0x8A;
    public final static byte CUSTOMIZED_SESSION_TARGET_INVENTORY = (byte) 0x8B;
    public final static byte SET_IMPINJ_FAST_TID = (byte) 0x8C;
    public final static byte SET_AND_SAVE_IMPINJ_FAST_TID = (byte) 0x8D;
    public final static byte GET_IMPINJ_FAST_TID = (byte) 0x8E;
    public final static byte ISO18000_6B_INVENTORY = (byte) 0xB0;
    public final static byte ISO18000_6B_READ_TAG = (byte) 0xB1;
    public final static byte ISO18000_6B_WRITE_TAG = (byte) 0xB2;
    public final static byte ISO18000_6B_LOCK_TAG = (byte) 0xB3;
    public final static byte ISO18000_6B_QUERY_LOCK_TAG = (byte) 0xB4;
    public final static byte GET_INVENTORY_BUFFER = (byte) 0x90;
    public final static byte GET_AND_RESET_INVENTORY_BUFFER = (byte) 0x91;
    public final static byte GET_INVENTORY_BUFFER_TAG_COUNT = (byte) 0x92;
    public final static byte RESET_INVENTORY_BUFFER = (byte) 0x93;

    public static String format(byte btCmd) {
        String strCmd = "";
        switch (btCmd) {
        case RESET:
            strCmd = "��λ��д��";
            break;
        case SET_UART_BAUDRATE:
            strCmd = "���ô���ͨѶ������";
            break;
        case GET_FIRMWARE_VERSION:
            strCmd = "��ȡ��д���̼��汾";
            break;
        case SET_READER_ADDRESS:
            strCmd = "���ö�д����ַ";
            break;
        case SET_WORK_ANTENNA:
            strCmd = "���ö�д����������";
            break;
        case GET_WORK_ANTENNA:
            strCmd = "��ѯ��ǰ���߹�������";
            break;
        case SET_OUTPUT_POWER:
            strCmd = "���ö�д����Ƶ�������";
            break;
        case GET_OUTPUT_POWER:
            strCmd = "��ѯ��д����ǰ�������";
            break;
        case SET_FREQUENCY_REGION:
            strCmd = "���ö�д������Ƶ�ʷ�Χ";
            break;
        case GET_FREQUENCY_REGION:
            strCmd = "��ѯ��д������Ƶ�ʷ�Χ";
            break;
        case SET_BEEPER_MODE:
            strCmd = "���÷�����״̬";
            break;
        case GET_READER_TEMPERATURE:
            strCmd = "��ѯ��ǰ�豸�Ĺ����¶�";
            break;
        case READ_GPIO_VALUE:
            strCmd = "��ȡGPIO��ƽ";
            break;
        case WRITE_GPIO_VALUE:
            strCmd = "����GPIO��ƽ";
            break;
        case SET_ANT_CONNECTION_DETECTOR:
            strCmd = "�����������Ӽ����״̬";
            break;
        case GET_ANT_CONNECTION_DETECTOR:
            strCmd = "��ȡ�������Ӽ����״̬";
            break;
        case SET_TEMPORARY_OUTPUT_POWER:
            strCmd = "���ö�д����ʱ��Ƶ�������";
            break;
        case SET_READER_IDENTIFIER:
            strCmd = "���ö�д��ʶ����";
            break;
        case GET_READER_IDENTIFIER:
            strCmd = "��ȡ��д��ʶ����";
            break;
        case SET_RF_LINK_PROFILE:
            strCmd = "������Ƶ��·��ͨѶ����";
            break;
        case GET_RF_LINK_PROFILE:
            strCmd = "��ȡ��Ƶ��·��ͨѶ����";
            break;
        case GET_RF_PORT_RETURN_LOSS:
            strCmd = "�������߶˿ڵĻز����";
            break;
        case INVENTORY:
            strCmd = "�̴��ǩ";
            break;
        case READ_TAG:
            strCmd = "����ǩ";
            break;
        case WRITE_TAG:
            strCmd = "д��ǩ";
            break;
        case LOCK_TAG:
            strCmd = "������ǩ";
            break;
        case KILL_TAG:
            strCmd = "����ǩ";
            break;
        case SET_ACCESS_EPC_MATCH:
            strCmd = "ƥ��ACCESS������EPC��";
            break;
        case GET_ACCESS_EPC_MATCH:
            strCmd = "��ѯƥ���EPC״̬";
            break;
        case REAL_TIME_INVENTORY:
            strCmd = "�̴��ǩ(ʵʱ�ϴ���ǩ����)";
            break;
        case FAST_SWITCH_ANT_INVENTORY:
            strCmd = "������ѯ��������̴��ǩ";
            break;
        case CUSTOMIZED_SESSION_TARGET_INVENTORY:
            strCmd = "�Զ���session��target�̴�";
            break;
        case SET_IMPINJ_FAST_TID:
            strCmd = "����Monza��ǩ���ٶ�TID(������)";
            break;
        case SET_AND_SAVE_IMPINJ_FAST_TID:
            strCmd = "����Monza��ǩ���ٶ�TID(����)";
            break;
        case GET_IMPINJ_FAST_TID:
            strCmd = "��ѯ��ǰ�Ŀ���TID����";
            break;
        case ISO18000_6B_INVENTORY:
            strCmd = "�̴�18000-6B��ǩ";
            break;
        case ISO18000_6B_READ_TAG:
            strCmd = "��18000-6B��ǩ";
            break;
        case ISO18000_6B_WRITE_TAG:
            strCmd = "д18000-6B��ǩ";
            break;
        case ISO18000_6B_LOCK_TAG:
            strCmd = "����18000-6B��ǩ";
            break;
        case ISO18000_6B_QUERY_LOCK_TAG:
            strCmd = "��ѯ18000-6B��ǩ";
            break;
        case GET_INVENTORY_BUFFER:
            strCmd = "��ȡ��ǩ���ݱ������汸��";
            break;
        case GET_AND_RESET_INVENTORY_BUFFER:
            strCmd = "��ȡ��ǩ���ݲ�ɾ������";
            break;
        case GET_INVENTORY_BUFFER_TAG_COUNT:
            strCmd = "��ѯ�������Ѷ���ǩ����";
            break;
        case RESET_INVENTORY_BUFFER:
            strCmd = "��ձ�ǩ���ݻ���";
            break;
        default:
            strCmd = "δ֪����";
            break;
        }
        return strCmd;
    }
}
