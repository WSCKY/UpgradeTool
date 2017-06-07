package protocol;

import protocol.math.CalculateCRC;

public class ComPackage implements Cloneable {
	/* file data cache size */
	public static final int FILE_DATA_CACHE = 80;

	private static final byte Header1 = (byte)0x55;
	private static final byte Header2 = (byte)0xAA;
	private static final char CRC_INIT = (char)0x66;
	private static final int CACHE_SIZE = FILE_DATA_CACHE + 5;

	/* package type */
	public static final byte TYPE_UPGRADE_REQUEST = (byte)0x80;
	public static final byte TYPE_UPGRADE_DATA = (byte)0x81;
	public static final byte TYPE_UPGRADE_FC_ACK = (byte)0x82;
	/* firmware type */
	public static final byte FW_TYPE_NONE = (byte)0x0;
	public static final byte FW_TYPE_FC = (byte)0x1;

	public static final byte FC_STATE_READY = (byte)0x0;
	public static final byte FC_STATE_ERASE = (byte)0x1;
	public static final byte FC_STATE_UPGRADE = (byte)0x2;
	public static final byte FC_STATE_REFUSED = (byte)0x3;
	public static final byte FC_STATE_JUMPFAILED = (byte)0x4;

	public static final byte FC_REFUSED_BUSY = (byte)0x0;
	public static final byte FC_REFUSED_VERSION_OLD = (byte)0x1;
	public static final byte FC_REFUSED_OVER_SIZE = (byte)0x2;
	public static final byte FC_REFUSED_TYPE_ERROR = (byte)0x3;
	public static final byte FC_REFUSED_LOW_VOLTAGE = (byte)0x4;
	public static final byte FC_REFUSED_UNKNOWERROR = (byte)0x5;

	public byte stx1;
	public byte stx2;
	public int length;
	public int type;
	public byte[] rData;
	public char crc;

	public ComPackage() {
		stx1 = Header1;
		stx2 = Header2;
		length = 0;
		type = 0;
		rData = new byte[CACHE_SIZE];
		crc = 0;
	}

	public void setLength(int len) {
		length = len;
	}

	public void addBytes(byte[] c, int len, int pos) {
		System.arraycopy(c, 0, rData, pos, len);
	}
	public void addByte(byte c, int pos) {
		rData[pos] = c;
	}
	public void addFloat(float f, int pos) {
		int d = Float.floatToRawIntBits(f);
		byte[] c = new byte[]{(byte)(d >> 0), (byte)(d >> 8), (byte)(d >> 16), (byte)(d >> 24)};
		addBytes(c, 4, pos);
	}
	public void addInteger(int d, int pos) {
		byte[] c = new byte[]{(byte)(d >> 0), (byte)(d >> 8), (byte)(d >> 16), (byte)(d >> 24)};
		addBytes(c, 4, pos);
	}
	public void addCharacter(char d, int pos) {
		byte[] c = new byte[]{(byte)(d >> 8), (byte)(d >> 0)};
		addBytes(c, 2, pos);
	}
	public float readoutFloat(int pos) {//...
		return 0.0f;
	}
	public int readoutInteger(int pos) {
		int c = (rData[pos] & 0xFF) | ((rData[pos + 1] << 8) & 0xFF00) | ((rData[pos + 2] << 24) >>> 8) | (rData[pos + 3] << 24);
		return c;
	}

	public byte[] getCRCBuffer() {
		byte[] c = new byte[length];
		System.arraycopy(rData, 0, c, 2, length - 2);
		c[0] = (byte)length;
		c[1] = (byte)type;
		return c;
	}

	public byte[] getSendBuffer() {
		byte[] c = new byte[length + 3];
		c[0] = stx1;
		c[1] = stx2;
		c[2] = (byte)length;
		c[3] = (byte)type;
		System.arraycopy(rData, 0, c, 4, length - 2);
		c[length + 2] = ComputeCRC();
		return c;
	}

	public byte ComputeCRC() {
		return (byte)CalculateCRC.ComputeCRC8(getCRCBuffer(), length, CRC_INIT);
	}

	public Object PackageCopy() throws CloneNotSupportedException {
		return super.clone();
	} 
}
