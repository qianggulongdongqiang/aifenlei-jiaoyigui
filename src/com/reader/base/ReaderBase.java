/**
 * �ļ��� ��ReaderMethod.java
 * CopyRright (c) 2008-xxxx:
 * �ļ���ţ�2014-03-12_001
 * �����ˣ�Jie Zhu
 * ���ڣ�2014/03/12
 * �޸��ˣ�Jie Zhu
 * ���ڣ�2014/03/12
 * ��������ʼ�汾
 * �汾��1.0.0
 */

package com.reader.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.reader.base.CMD;
import com.reader.base.HEAD;

public abstract class ReaderBase {
    private WaitThread mWaitThread = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;

    /**
     * ���Ӷ�ʧ��
     */
    public abstract void onLostConnect();

    /**
     * ����д������������һ�����ݺ����ô˺�����
     * 
     * @param msgTran
     *            �������İ�
     */
    public abstract void analyData(MessageTran msgTran);

    // ��¼δ����Ľ������ݣ���Ҫ���ǽ������ݷֶ�
    private byte[] m_btAryBuffer = new byte[4096];
    // ��¼δ�������ݵ���Ч����
    private int m_nLength = 0;

    /**
     * ���ι��캯��������һ�������롢�������Reader��
     * 
     * @param in
     *            ������
     * @param out
     *            �����
     */
    public ReaderBase(InputStream in, OutputStream out) {
        this.mInStream = in;
        this.mOutStream = out;

        StartWait();
    }

    public boolean IsAlive() {
        return mWaitThread != null && mWaitThread.isAlive();
    }

    public void StartWait() {
        mWaitThread = new WaitThread();
        mWaitThread.start();
    }

    /**
     * ѭ�����������̡߳�
     * 
     * @author Jie
     */
    private class WaitThread extends Thread {
        private boolean mShouldRunning = true;

        public WaitThread() {
            mShouldRunning = true;
        }

        @Override
        public void run() {
            byte[] btAryBuffer = new byte[4096];
            while (mShouldRunning) {
                try {
                    int nLenRead = mInStream.read(btAryBuffer);

                    if (nLenRead > 0) {
                        byte[] btAryReceiveData = new byte[nLenRead];
                        System.arraycopy(btAryBuffer, 0, btAryReceiveData, 0,
                                nLenRead);
                        runReceiveDataCallback(btAryReceiveData);
                    }
                } catch (IOException e) {
                    onLostConnect();
                    return;
                } catch (Exception e) {
                    onLostConnect();
                    return;
                }
            }
        }

        public void signOut() {
            mShouldRunning = false;
            interrupt();
        }
    };

    /**
     * �˳������̡߳�
     */
    public final void signOut() {
        mWaitThread.signOut();
        try {
            mInStream.close();
            mOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ����������ȡ���ݺ󣬻���ô˺�����
     * 
     * @param btAryReceiveData
     *            ���յ�������
     */
    private void runReceiveDataCallback(byte[] btAryReceiveData) {
        try {
            reciveData(btAryReceiveData);

            int nCount = btAryReceiveData.length;
            byte[] btAryBuffer = new byte[nCount + m_nLength];

            System.arraycopy(m_btAryBuffer, 0, btAryBuffer, 0, m_nLength);
            System.arraycopy(btAryReceiveData, 0, btAryBuffer, m_nLength,
                    btAryReceiveData.length);

            // �����������ݣ���0xA0Ϊ������㣬��Э�������ݳ���Ϊ������ֹ��
            int nIndex = 0; // �������д���A0ʱ����¼���ݵ���ֹ��
            int nMarkIndex = 0; // �������в�����A0ʱ��nMarkIndex�����������������
            for (int nLoop = 0; nLoop < btAryBuffer.length; nLoop++) {
                if (btAryBuffer.length > nLoop + 1) {
                    if (btAryBuffer[nLoop] == HEAD.HEAD) {
                        int nLen = btAryBuffer[nLoop + 1] & 0xFF;
                        if (nLoop + 1 + nLen < btAryBuffer.length) {
                            byte[] btAryAnaly = new byte[nLen + 2];
                            System.arraycopy(btAryBuffer, nLoop, btAryAnaly, 0,
                                    nLen + 2);

                            MessageTran msgTran = new MessageTran(btAryAnaly);
                            analyData(msgTran);

                            nLoop += 1 + nLen;
                            nIndex = nLoop + 1;
                        } else {
                            nLoop += 1 + nLen;
                        }
                    } else {
                        nMarkIndex = nLoop;
                    }
                }
            }

            if (nIndex < nMarkIndex) {
                nIndex = nMarkIndex + 1;
            }

            if (nIndex < btAryBuffer.length) {
                m_nLength = btAryBuffer.length - nIndex;
                Arrays.fill(m_btAryBuffer, 0, 4096, (byte) 0);
                System.arraycopy(btAryBuffer, nIndex, m_btAryBuffer, 0,
                        btAryBuffer.length - nIndex);
            } else {
                m_nLength = 0;
            }
        } catch (Exception e) {

        }
    }

    /**
     * ����д���������յ�����ʱ����ô˺�����
     * 
     * @param btAryReceiveData
     *            �յ�������
     */
    public void reciveData(byte[] btAryReceiveData) {
    };

    /**
     * ����д�������������ݺ����ô˺�����
     * 
     * @param btArySendData
     *            ���͵�����
     */
    public void sendData(byte[] btArySendData) {
    };

    /**
     * �������ݣ�ʹ��synchronized()��ֹ����������
     * 
     * @param btArySenderData
     *            Ҫ���͵�����
     * @return �ɹ� :0, ʧ��:-1
     */
    private int sendMessage(byte[] btArySenderData) {

        try {
            synchronized (mOutStream) { // ��ֹ����
                mOutStream.write(btArySenderData);
            }
        } catch (IOException e) {
            onLostConnect();
            return -1;
        } catch (Exception e) {
            return -1;
        }

        sendData(btArySenderData);

        return 0;
    }

    private int sendMessage(byte btReadId, byte btCmd) {
        MessageTran msgTran = new MessageTran(btReadId, btCmd);

        return sendMessage(msgTran.getAryTranData());
    }

    private int sendMessage(byte btReadId, byte btCmd, byte[] btAryData) {
        MessageTran msgTran = new MessageTran(btReadId, btCmd, btAryData);

        return sendMessage(msgTran.getAryTranData());
    }

    /**
     * ��λָ����ַ�Ķ�д����
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int reset(byte btReadId) {

        byte btCmd = CMD.RESET;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ���ô���ͨѶ�����ʡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param nIndexBaudrate
     *            ������(0x03: 38400bps, 0x04:115200 bps)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setUartBaudrate(byte btReadId, byte nIndexBaudrate) {
        byte btCmd = CMD.SET_UART_BAUDRATE;
        byte[] btAryData = new byte[1];

        btAryData[0] = nIndexBaudrate;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ȡ��д���̼��汾��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getFirmwareVersion(byte btReadId) {
        byte btCmd = CMD.GET_FIRMWARE_VERSION;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ���ö�д����ַ��
     * 
     * @param btReadId
     *            ԭ��д����ַ(0xFF���õ�ַ)
     * @param btNewReadId
     *            �¶�д����ַ,ȡֵ��Χ0-254(0x00�C0xFE)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setReaderAddress(byte btReadId, byte btNewReadId) {
        byte btCmd = CMD.SET_READER_ADDRESS;
        byte[] btAryData = new byte[1];

        btAryData[0] = btNewReadId;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ���ö�д���������ߡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btWorkAntenna
     *            ���ߺ�(0x00:����1, 0x01:����2, 0x02:����3, 0x03:����4)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setWorkAntenna(byte btReadId, byte btWorkAntenna) {
        byte btCmd = CMD.SET_WORK_ANTENNA;
        byte[] btAryData = new byte[1];

        btAryData[0] = btWorkAntenna;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ѯ��ǰ���߹������ߡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getWorkAntenna(byte btReadId) {
        byte btCmd = CMD.GET_WORK_ANTENNA;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ���ö�д����Ƶ�������(��ʽ1)�� <br>
     * ��������ʱ������100mS�� <br>
     * �������Ҫ��̬�ı���Ƶ������ʣ���ʹ��CmdCode_set_temporary_output_power������򽫻�Ӱ��Flash��ʹ��������
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btOutputPower
     *            RF�������,ȡֵ��Χ0-33(0x00�C0x21), ��λdBm
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setOutputPower(byte btReadId, byte btOutputPower) {
        byte btCmd = CMD.SET_OUTPUT_POWER;
        byte[] btAryData = new byte[1];

        btAryData[0] = btOutputPower;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ���ö�д����Ƶ�������(��ʽ2)�� <br>
     * ��������ʱ������100mS�� <br>
     * �������Ҫ��̬�ı���Ƶ������ʣ���ʹ��CmdCode_set_temporary_output_power������򽫻�Ӱ��Flash��ʹ��������
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btPower1
     *            RF����1�������,ȡֵ��Χ0-33(0x00�C0x21), ��λdBm
     * @param btPower2
     *            RF����2�������,ȡֵ��Χ0-33(0x00�C0x21), ��λdBm
     * @param btPower3
     *            RF����3�������,ȡֵ��Χ0-33(0x00�C0x21), ��λdBm
     * @param btPower4
     *            RF����4�������,ȡֵ��Χ0-33(0x00�C0x21), ��λdBm
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setOutputPower(byte btReadId, byte btPower1,
            byte btPower2, byte btPower3, byte btPower4) {
        byte btCmd = CMD.SET_OUTPUT_POWER;
        byte[] btAryData = new byte[4];

        btAryData[0] = btPower1;
        btAryData[1] = btPower2;
        btAryData[2] = btPower3;
        btAryData[3] = btPower4;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ѯ��д����ǰ������ʡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getOutputPower(byte btReadId) {
        byte btCmd = CMD.GET_OUTPUT_POWER;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ���ö�д������Ƶ�ʷ�Χ(ʹ��ϵͳĬ�ϵ�Ƶ��)��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btRegion
     *            ��Ƶ�淶(0x01:FCC, 0x02:ETSI, 0x03:CHN)
     * @param btStartRegion
     *            Ƶ����ʼ��,
     * @param btEndRegion
     *            Ƶ�ʽ�����,��������Ƶ�淶��Ƶ�ʷ�Χ����������Ƶ�ķ�Χ. ���������ù���Ϊ: 1,��ʼƵ�������Ƶ�ʲ��ܳ�����Ƶ�淶�ķ�Χ.
     *            2,��ʼƵ�ʱ�����ڽ���Ƶ��. 3,��ʼƵ�ʵ��ڽ���Ƶ����Ƶ����
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setFrequencyRegion(byte btReadId, byte btRegion,
            byte btStartRegion, byte btEndRegion) {
        byte btCmd = CMD.SET_FREQUENCY_REGION;
        byte[] btAryData = new byte[3];

        btAryData[0] = btRegion;
        btAryData[1] = btStartRegion;
        btAryData[2] = btEndRegion;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ���ö�д������Ƶ�ʷ�Χ(�û��Զ���Ƶ��)��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btFreqInterval
     *            Ƶ����, Ƶ���� = btFreqInterval x 10KHz
     * @param btChannelQuantity
     *            Ƶ������, ������ʼƵ�ʵ�Ƶ������, 1Ϊ����ʼƵ�ʶ�Ƶ����. �˲����������0
     * @param nStartFreq
     *            ��ʼƵ��, ��λΪKHz. 16��������λ��ǰ. ����915000KHz����0x0D 0xF6 0x38
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setUserDefineFrequency(byte btReadId, byte btFreqInterval,
            byte btChannelQuantity, int nStartFreq) {
        byte btCmd = CMD.SET_FREQUENCY_REGION;
        byte[] btAryFreq = new byte[3];
        byte[] btAryData = new byte[6];

        btAryFreq = Converter.getBytes(nStartFreq, Converter.BIG_ENDIAN);

        btAryData[0] = 4;
        btAryData[1] = btFreqInterval;
        btAryData[2] = btChannelQuantity;
        btAryData[3] = btAryFreq[2];
        btAryData[4] = btAryFreq[1];
        btAryData[5] = btAryFreq[0];

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ѯ��д������Ƶ�ʷ�Χ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getFrequencyRegion(byte btReadId) {
        byte btCmd = CMD.GET_FREQUENCY_REGION;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ���÷�����״̬�� <br>
     * �����һ�ű�ǩ����������죬��ռ�ô���������ʱ�䣬����ѡ��򿪣���������Ӱ�쵽�����ǩ(����ͻ�㷨)�����ܣ���ѡ��Ӧ��Ϊ���Թ���ѡ�á�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btMode
     *            ������ǩʱ������״̬(0x00:����, 0x01:ÿ���̴������, 0x02:ÿ����һ�ű�ǩ����)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setBeeperMode(byte btReadId, byte btMode) {
        byte btCmd = CMD.SET_BEEPER_MODE;
        byte[] btAryData = new byte[1];

        btAryData[0] = btMode;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ѯ��ǰ�豸�Ĺ����¶ȡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getReaderTemperature(byte btReadId) {
        byte btCmd = CMD.GET_READER_TEMPERATURE;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ��ȡGPIO��ƽ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int readGpioValue(byte btReadId) {
        byte btCmd = CMD.READ_GPIO_VALUE;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ����GPIO��ƽ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btChooseGpio
     *            GPIO��ѡ��(0x03:����GPIO3, 0x04:����GPIO4)
     * @param btGpioValue
     *            GPIO����ֵ(0x00:�͵�ƽ, 0x01:�ߵ�ƽ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int writeGpioValue(byte btReadId, byte btChooseGpio,
            byte btGpioValue) {
        byte btCmd = CMD.WRITE_GPIO_VALUE;
        byte[] btAryData = new byte[2];

        btAryData[0] = btChooseGpio;
        btAryData[1] = btGpioValue;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * �����������Ӽ����״̬��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btDetectorStatus
     *            ״̬(0x00:�ر��������Ӽ��, ����ֵ:�������Ӽ���������(�˿ڻز����ֵ),��λdB.
     *            ֵԽ��Զ˿ڵ��迹ƥ��Ҫ��Խ��
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setAntConnectionDetector(byte btReadId,
            byte btDetectorStatus) {
        byte btCmd = CMD.SET_ANT_CONNECTION_DETECTOR;
        byte[] btAryData = new byte[1];

        btAryData[0] = btDetectorStatus;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ȡ�������Ӽ����״̬��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getAntConnectionDetector(byte btReadId) {
        byte btCmd = CMD.GET_ANT_CONNECTION_DETECTOR;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ���ö�д����ʱ��Ƶ������ʡ� <br>
     * �����ɹ����������ֵ�����ᱻ�������ڲ���Flash�У�����������ϵ��������ʽ��ָ����ڲ�Flash�б�����������ֵ��������Ĳ����ٶȷǳ��죬
     * ���Ҳ�дFlash���Ӷ���Ӱ��Flash��ʹ���������ʺ���Ҫ�����л���Ƶ������ʵ�Ӧ�á�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btRfPower
     *            RF�������, ȡֵ��Χ20-33(0x14�C0x21), ��λdBm
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setTemporaryOutputPower(byte btReadId, byte btRfPower) {
        byte btCmd = CMD.SET_TEMPORARY_OUTPUT_POWER;
        byte[] btAryData = new byte[1];

        btAryData[0] = btRfPower;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ���ö�д��ʶ���롣 <br>
     * �����ɹ���12�ֽڵĶ�д��ʶ���ַ������ᱣ�����ڲ���Flash�У��ϵ�󲻶�ʧ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryIdentifier
     *            12�ֽڵĶ�д��ʶ���ַ�
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setReaderIdentifier(byte btReadId, byte[] btAryIdentifier) {
        byte btCmd = CMD.SET_READER_IDENTIFIER;

        int nResult = sendMessage(btReadId, btCmd, btAryIdentifier);

        return nResult;
    }

    /**
     * ��ȡ��д��ʶ���롣
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getReaderIdentifier(byte btReadId) {
        byte btCmd = CMD.GET_READER_IDENTIFIER;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ������Ƶ��·��ͨѶ���ʡ� <br>
     * �����ɹ����д�����������������ñ������ڲ���Flash�У��ϵ�󲻶�ʧ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btProfile
     *            ��Ƶ��·��ͨѶ����(0xD0:Tari 25uS,FM0 40KHz, 0xD1:Tari 25uS,Miller 4
     *            250KHz, 0xD2:Tari 25uS,Miller 4 300KHz, 0xD3:Tari 6.25uS,FM0
     *            400KHz)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setRfLinkProfile(byte btReadId, byte btProfile) {
        byte btCmd = CMD.SET_RF_LINK_PROFILE;
        byte[] btAryData = new byte[1];

        btAryData[0] = btProfile;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ȡ��Ƶ��·��ͨѶ���ʡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getRfLinkProfile(byte btReadId) {
        byte btCmd = CMD.GET_RF_LINK_PROFILE;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * �������߶˿ڵĻز���ġ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btFrequency
     *            Ƶ�ʲ���, ϵͳ����ȡ��Ƶ�㵱ǰ�������߶˿ڵĻز����ֵ
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getRfPortReturnLoss(byte btReadId, byte btFrequency) {
        byte btCmd = CMD.GET_RF_PORT_RETURN_LOSS;
        byte[] btAryData = new byte[1];

        btAryData[0] = btFrequency;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * �̴��ǩ�� <br>
     * ��д���յ�������󣬽��ж��ǩʶ���������ǩ���ݴ����д���������� <br>
     * ע�⣺ <br>
     * �ｫ�������ó�255(0xFF)ʱ��������רΪ��������ǩ��Ƶ��㷨�������������Ӧ����˵��Ч�ʸ��ߣ���Ӧ��������
     * ���˲������ʺ�ͬʱ��ȡ������ǩ��Ӧ�á�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btRepeat
     *            �̴�����ظ��Ĵ���, Repeat=0xFF������̴�ʱ��Ϊ���ʱ��.
     *            �����Ƶ������ֻ��һ�ű�ǩ,����ֵ��̴�Լ��ʱΪ30-50mS. һ������ͨ�������Ͽ�����ѯ�������ʱʹ�ô˲���ֵ
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int inventory(byte btReadId, byte btRepeat) {
        byte btCmd = CMD.INVENTORY;
        byte[] btAryData = new byte[1];

        btAryData[0] = btRepeat;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ����ǩ�� <br>
     * ע�⣺ <br>
     * ����ͬEPC�ı�ǩ������ȡ�����ݲ���ͬ������Ϊ��ͬ�ı�ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btMemBank
     *            ��ǩ�洢����(0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER)
     * @param btWordAdd
     *            ��ȡ�����׵�ַ,ȡֵ��Χ��ο���ǩ���
     * @param btWordCnt
     *            ��ȡ���ݳ���,�ֳ�,WORD(16 bits)����. ȡֵ��Χ��ο���ǩ�����
     * @param btAryPassWord
     *            ��ǩ��������,4�ֽ�
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int readTag(byte btReadId, byte btMemBank, byte btWordAdd,
            byte btWordCnt, byte[] btAryPassWord) {
        byte btCmd = CMD.READ_TAG;
        byte[] btAryData = null;
        if (btAryPassWord == null || btAryPassWord.length < 4) {
            btAryPassWord = null;
            btAryData = new byte[3];
        } else {
            btAryData = new byte[3 + 4];
        }

        btAryData[0] = btMemBank;
        btAryData[1] = btWordAdd;
        btAryData[2] = btWordCnt;

        if (btAryPassWord != null) {
            System.arraycopy(btAryPassWord, 0, btAryData, 3,
                    btAryPassWord.length);
        }

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * д��ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryPassWord
     *            ��ǩ��������,4�ֽ�
     * @param btMemBank
     *            ��ǩ�洢����(0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER)
     * @param btWordAdd
     *            �����׵�ַ,WORD(16 bits)��ַ. д��EPC�洢����һ���0x02��ʼ,������ǰ�ĸ��ֽڴ��PC+CRC
     * @param btWordCnt
     *            WORD(16 bits)����,��ֵ��ο���ǩ���
     * @param btAryData
     *            д�������, btWordCnt*2 �ֽ�
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int writeTag(byte btReadId, byte[] btAryPassWord,
            byte btMemBank, byte btWordAdd, byte btWordCnt, byte[] btAryData) {
        byte btCmd = CMD.WRITE_TAG;
        byte[] btAryBuffer = new byte[btAryData.length + 7];

        System.arraycopy(btAryPassWord, 0, btAryBuffer, 0, btAryPassWord.length);
        // btAryPassWord.CopyTo(btAryBuffer, 0);
        btAryBuffer[4] = btMemBank;
        btAryBuffer[5] = btWordAdd;
        btAryBuffer[6] = btWordCnt;
        System.arraycopy(btAryData, 0, btAryBuffer, 7, btAryData.length);
        // btAryData.CopyTo(btAryBuffer, 7);

        int nResult = sendMessage(btReadId, btCmd, btAryBuffer);

        return nResult;
    }

    /**
     * ������ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryPassWord
     *            ��ǩ��������,4�ֽ�
     * @param btMembank
     *            ��������������(0x01:User Memory, 0x02:TID Memory, 0x03:EPC Memory,
     *            0x04:Access Password, 0x05:Kill Password)
     * @param btLockType
     *            ����������(0x00:����, 0x01:����, 0x02:���ÿ���, 0x03:��������)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int lockTag(byte btReadId, byte[] btAryPassWord,
            byte btMemBank, byte btLockType) {
        byte btCmd = CMD.LOCK_TAG;
        byte[] btAryData = new byte[6];

        System.arraycopy(btAryPassWord, 0, btAryData, 0, btAryPassWord.length);
        // btAryPassWord.CopyTo(btAryData, 0);
        btAryData[4] = btMemBank;
        btAryData[5] = btLockType;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ����ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryPassWord
     *            ��ǩ��������,4�ֽ�
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int killTag(byte btReadId, byte[] btAryPassWord) {
        byte btCmd = CMD.KILL_TAG;
        byte[] btAryData = new byte[4];

        System.arraycopy(btAryPassWord, 0, btAryData, 0, btAryPassWord.length);
        // btAryPassWord.CopyTo(btAryData, 0);

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ƥ��ACCESS������EPC��(ƥ��һֱ��Ч��ֱ����һ��ˢ��)��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btEpcLen
     *            EPC����
     * @param btAryEpc
     *            EPC��, ��EpcLen���ֽ����
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setAccessEpcMatch(byte btReadId, byte btEpcLen,
            byte[] btAryEpc) {
        byte btCmd = CMD.SET_ACCESS_EPC_MATCH;
        int nLen = (btEpcLen & 0xFF) + 2;
        byte[] btAryData = new byte[nLen];

        btAryData[0] = 0x00;
        btAryData[1] = btEpcLen;
        System.arraycopy(btAryEpc, 0, btAryData, 2, btAryEpc.length);
        // btAryEpc.CopyTo(btAryData, 2);

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ���EPCƥ�䡣
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int cancelAccessEpcMatch(byte btReadId) {
        byte btCmd = CMD.SET_ACCESS_EPC_MATCH;
        byte[] btAryData = new byte[1];
        btAryData[0] = 0x01;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ѯƥ���EPC״̬��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getAccessEpcMatch(byte btReadId) {
        byte btCmd = CMD.GET_ACCESS_EPC_MATCH;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * �̴��ǩ(ʵʱ�ϴ���ǩ����)�� <br>
     * ע�⣺ <br>
     * ������Ӳ��Ϊ˫CPU�ܹ�����CPU������ѯ��ǩ����CPU�������ݹ�����ѯ��ǩ�ͷ������ݲ��У�����ռ�öԷ���ʱ�䣬
     * ��˴��ڵ����ݴ��䲻Ӱ���д��������Ч�ʡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btRepeat
     *            �̴�����ظ��Ĵ���,Repeat=0xFF������̴�ʱ��Ϊ���ʱ��.
     *            �����Ƶ������ֻ��һ�ű�ǩ,����ֵ��̴�Լ��ʱΪ30-50mS. һ������ͨ�������Ͽ�����ѯ�������ʱʹ�ô˲���ֵ
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int realTimeInventory(byte btReadId, byte btRepeat) {
        byte btCmd = CMD.REAL_TIME_INVENTORY;
        byte[] btAryData = new byte[1];

        btAryData[0] = btRepeat;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ������ѯ��������̴��ǩ�� <br>
     * ע�⣺ <br>
     * ������Ӳ��Ϊ˫CPU�ܹ�����CPU������ѯ��ǩ����CPU�������ݹ�����ѯ��ǩ�ͷ������ݲ��У�����ռ�öԷ���ʱ�䣬
     * ��˴��ڵ����ݴ��䲻Ӱ���д��������Ч�ʡ� <br>
     * ��������ȡ������ǩʱ��Ч��û��CmdCode_real_time_inventory����ߡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btA
     *            ������ѯ������(00�C03), ���ߺŴ��������ʾ����ѯ
     * @param btStayA
     *            �����ظ���ѯ�Ĵ���
     * @param btB
     *            �ڶ�����ѯ������(00�C03), ���ߺŴ��������ʾ����ѯ
     * @param btStayB
     *            �����ظ���ѯ�Ĵ���
     * @param btC
     *            ��������ѯ������(00�C03), ���ߺŴ��������ʾ����ѯ
     * @param btStayC
     *            �����ظ���ѯ�Ĵ���
     * @param btD
     *            ���ĸ���ѯ������(00�C03), ���ߺŴ��������ʾ����ѯ
     * @param btStayD
     *            �����ظ���ѯ�Ĵ���
     * @param btInterval
     *            ���߼����Ϣʱ��. ��λ��mS. ��Ϣʱ����Ƶ���,�ɽ��͹���
     * @param btRepeat
     *            �ظ����������л�˳�����
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int fastSwitchAntInventory(byte btReadId, byte btA,
            byte btStayA, byte btB, byte btStayB, byte btC, byte btStayC,
            byte btD, byte btStayD, byte btInterval, byte btRepeat) {

        byte btCmd = CMD.FAST_SWITCH_ANT_INVENTORY;
        byte[] btAryData = new byte[10];

        btAryData[0] = btA;
        btAryData[1] = btStayA;
        btAryData[2] = btB;
        btAryData[3] = btStayB;
        btAryData[4] = btC;
        btAryData[5] = btStayC;
        btAryData[6] = btD;
        btAryData[7] = btStayD;
        btAryData[8] = btInterval;
        btAryData[9] = btRepeat;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * �Զ���session��target�̴档
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btSession
     *            ָ���̴��session
     * @param btTarget
     *            ָ���̴��Inventoried Flag(0x00:A, 0x01:B)
     * @param btRepeat
     *            �̴�����ظ��Ĵ���
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int customizedSessionTargetInventory(byte btReadId,
            byte btSession, byte btTarget, byte btRepeat) {

        byte btCmd = CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY;
        byte[] btAryData = new byte[3];

        btAryData[0] = btSession;
        btAryData[1] = btTarget;
        btAryData[2] = btRepeat;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ����Monza��ǩ���ٶ�TID�� <br>
     * ע�⣺ <br>
     * ��˹��ܽ���Impinj Monza ��ǩ�Ĳ����ͺ���Ч�� <br>
     * ��˹�����ʶ��EPC��ͬʱʶ��TID����˴������˶�TID��Ч�ʡ� <br>
     * ��򿪴˹��ܺ��ض��ͺŵı�ǩ�����̴�Ĺ����н�TID�����EPC�С���ˣ���ǩ��PC�ᱻ�޸ģ�ԭ����PC+EPC��Ϊ���޸ĺ��PC+EPC+(
     * EPC��CRC)+TID�� <br>
     * �������ʶ��TID�Ĺ����г��ִ������ϴ�ԭ����PC+EPC�� <br>
     * ���粻��Ҫ�˹����뽫��رգ����ⲻ��Ҫ��ʱ�����ġ� <br>
     * �������������ڲ���Flash�У������󽫻ظ���Flash�����ֵ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param blnOpen
     *            �Ƿ��FastTID
     * @param blnSave
     *            �Ƿ񱣴����ڲ�FLASH
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int setImpinjFastTid(byte btReadId, boolean blnOpen,
            boolean blnSave) {

        byte btCmd = (blnSave ? CMD.SET_AND_SAVE_IMPINJ_FAST_TID
                : CMD.SET_IMPINJ_FAST_TID);
        byte[] btAryData = new byte[1];

        btAryData[0] = (byte) (blnOpen ? 0x8D : 0x00);

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ѯ��ǰ�Ŀ���TID���á�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getImpinjFastTid(byte btReadId) {
        byte btCmd = CMD.GET_IMPINJ_FAST_TID;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * �̴�18000-6B��ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int iso180006BInventory(byte btReadId) {
        byte btCmd = CMD.ISO18000_6B_INVENTORY;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ��18000-6B��ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryUID
     *            ��������ǩ��UID, 8 bytes
     * @param btWordAdd
     *            Ҫ��ȡ�������׵�ַ
     * @param btWordCnt
     *            Ҫ��ȡ�����ݳ���
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int iso180006BReadTag(byte btReadId, byte[] btAryUID,
            byte btWordAdd, byte btWordCnt) {
        byte btCmd = CMD.ISO18000_6B_READ_TAG;
        int nLen = btAryUID.length + 2;
        byte[] btAryData = new byte[nLen];

        System.arraycopy(btAryUID, 0, btAryData, 0, btAryUID.length);
        // btAryUID.CopyTo(btAryData, 0);
        btAryData[nLen - 2] = btWordAdd;
        btAryData[nLen - 1] = btWordCnt;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * д18000-6B��ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryUID
     *            ��������ǩ��UID, 8 bytes
     * @param btWordAdd
     *            д�����ݵ��׵�ַ
     * @param btWordCnt
     *            д�����ݵĳ���
     * @param btAryBuffer
     *            д�������
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int iso180006BWriteTag(byte btReadId, byte[] btAryUID,
            byte btWordAdd, byte btWordCnt, byte[] btAryBuffer) {
        byte btCmd = CMD.ISO18000_6B_WRITE_TAG;
        int nLen = btAryUID.length + 2 + btAryBuffer.length;
        byte[] btAryData = new byte[nLen];

        System.arraycopy(btAryUID, 0, btAryData, 0, btAryUID.length);
        // btAryUID.CopyTo(btAryData, 0);
        btAryData[btAryUID.length] = btWordAdd;
        btAryData[btAryUID.length + 1] = btWordCnt;
        System.arraycopy(btAryBuffer, 0, btAryData, btAryUID.length + 2,
                btAryBuffer.length);
        // btAryBuffer.CopyTo(btAryData, btAryUID.length + 2);

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ����18000-6B��ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryUID
     *            ��������ǩ��UID, 8 bytes
     * @param btWordAdd
     *            �������ĵ�ַ
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int iso180006BLockTag(byte btReadId, byte[] btAryUID,
            byte btWordAdd) {
        byte btCmd = CMD.ISO18000_6B_LOCK_TAG;
        int nLen = btAryUID.length + 1;
        byte[] btAryData = new byte[nLen];

        System.arraycopy(btAryUID, 0, btAryData, 0, btAryUID.length);
        // btAryUID.CopyTo(btAryData, 0);
        btAryData[nLen - 1] = btWordAdd;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ѯ18000-6B��ǩ��
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @param btAryUID
     *            ��������ǩ��UID, 8 bytes
     * @param btWordAdd
     *            Ҫ��ѯ�ĵ�ַ
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int iso180006BQueryLockTag(byte btReadId, byte[] btAryUID,
            byte btWordAdd) {
        byte btCmd = CMD.ISO18000_6B_QUERY_LOCK_TAG;
        int nLen = btAryUID.length + 1;
        byte[] btAryData = new byte[nLen];

        System.arraycopy(btAryUID, 0, btAryData, 0, btAryUID.length);
        // btAryUID.CopyTo(btAryData, 0);
        btAryData[nLen - 1] = btWordAdd;

        int nResult = sendMessage(btReadId, btCmd, btAryData);

        return nResult;
    }

    /**
     * ��ȡ��ǩ���ݱ������汸�ݡ� <br>
     * ע�⣺ <br>
     * ��������ɺ󣬻����е����ݲ�����ʧ�����Զ����ȡ�� <br>
     * �����ٴ�����CmdCode_inventory ������̴浽�ı�ǩ���ۼƴ��뻺�档 <br>
     * �����ٴ�����������18000-6C��������е����ݽ�����ա�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getInventoryBuffer(byte btReadId) {
        byte btCmd = CMD.GET_INVENTORY_BUFFER;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ��ȡ��ǩ���ݲ�ɾ�����档
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getAndResetInventoryBuffer(byte btReadId) {
        byte btCmd = CMD.GET_AND_RESET_INVENTORY_BUFFER;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ��ѯ�������Ѷ���ǩ������
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int getInventoryBufferTagCount(byte btReadId) {
        byte btCmd = CMD.GET_INVENTORY_BUFFER_TAG_COUNT;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ��ձ�ǩ���ݻ��档
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int resetInventoryBuffer(byte btReadId) {
        byte btCmd = CMD.RESET_INVENTORY_BUFFER;

        int nResult = sendMessage(btReadId, btCmd);

        return nResult;
    }

    /**
     * ���������ݡ�
     * 
     * @param btReadId
     *            ��д����ַ(0xFF���õ�ַ)
     * @return �ɹ� :0, ʧ��:-1
     */
    public final int sendBuffer(byte[] btAryBuf) {

        int nResult = sendMessage(btAryBuf);

        return nResult;
    }
}
