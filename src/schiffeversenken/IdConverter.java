package schiffeversenken;

import java.math.BigInteger;
import java.util.Arrays;

import de.uniba.wiai.lspi.chord.data.ID;

public class IdConverter {
	
	public static final byte[] b = new byte[] { (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff };
	public static final ID maxID = new ID(b);
	public static final BigInteger maxIDBigInt = maxID.toBigInteger();

	public static BigInteger FieldToId(BigInteger end, BigInteger start,
			int field, int numberOfFields) {
		BigInteger result = BigInteger.ZERO;
		BigInteger fieldSize = getFieldSize(start, end, numberOfFields);
		BigInteger fieldBigInt = BigInteger.valueOf(field);

		result = fieldBigInt.multiply(fieldSize).add(start).add(BigInteger.ONE)
				.mod(IdConverter.maxIDBigInt);
		return result.add(fieldSize.divide(BigInteger.valueOf(2)));
	}

	public static ID FieldToId(ID opponent, ID opponentPredecessor, int field, int numberOfFields) {
		BigInteger result = FieldToId(opponent.toBigInteger(),
				opponentPredecessor.toBigInteger(), field, numberOfFields);
		byte[] b = result.toByteArray();
		// TODO führende Nullen prüfen?!?!
		if(b.length > 20) {
			b = Arrays.copyOfRange(b, 1, b.length);
		} else if(b.length< 20) {
			System.out.println("------------- ERROR FieldtoID byteArray too short: " + b.length);
		} 
		return new ID(b);
	}

	public static int IDtoField(ID startID, ID endID, ID targetID, int numberOfFields) {
		return IDtoField(startID.toBigInteger(), endID.toBigInteger(),
				targetID.toBigInteger(), numberOfFields);
	}

	public static int IDtoField(BigInteger start, BigInteger end,
			BigInteger target, int numberOfFields) {
		int result = 0;
		BigInteger fieldSize = IdConverter.getFieldSize(start, end, numberOfFields);
		if (start.compareTo(end) > 0) {
			result = target.subtract(start.subtract(IdConverter.maxIDBigInt))
					.divide(fieldSize).intValue();
		} else {
			result = target.subtract(start.add(BigInteger.ONE))
					.divide(fieldSize).intValue();
		}
		if (result == 100) {
			// Last field + rest
			result -= 1;
		}
		return result;
	}

	public static BigInteger getFieldSize(BigInteger start, BigInteger end, int numberOfFields) {
		BigInteger interval;

		if (end.compareTo(start) > 0) {
			interval = (end.subtract(start));
		} else {
			interval = maxIDBigInt.subtract(start).add(end);
		}
		return interval.divide(BigInteger.valueOf(numberOfFields));
	}

	public static BigInteger getFieldSize(ID startID, ID endID, int numberOfFields) {
		return getFieldSize(startID.toBigInteger(), endID.toBigInteger(), numberOfFields);
	}

}
