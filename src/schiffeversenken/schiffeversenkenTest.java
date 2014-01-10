package schiffeversenken;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import de.uniba.wiai.lspi.chord.data.ID;

public class schiffeversenkenTest {

	
	public void testIdToField() {
		ID id1 = new ID(BigInteger.valueOf(1009).toByteArray());
		ID id2 = new ID(BigInteger.valueOf(2022).toByteArray());
		ID targetID = new ID(BigInteger.valueOf(1667).toByteArray());
		int field = IdConverter.IDtoField(id1, id2, targetID,100);
		assertEquals(field,66);
	}
	
	@Test
	public void testFieldSize() {
		BigInteger int1 = BigInteger.valueOf(1000);
		BigInteger int2 = BigInteger.valueOf(2100);
		BigInteger target = BigInteger.valueOf(11);
		
		assertEquals(target,IdConverter.getFieldSize(int1, int2,100));
		
	}
	
	@Test
	public void testFieldSize1() {
		BigInteger int1 = BigInteger.valueOf(999);
		BigInteger int2 = BigInteger.valueOf(2188);
		BigInteger target = BigInteger.valueOf(11);
		
		assertEquals(target,IdConverter.getFieldSize(int1, int2,100));
	}
	
	@Test
	public void testFieldSize2() {
		BigInteger int1 = IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(100));
		BigInteger int2 = BigInteger.valueOf(1000);
		BigInteger target = BigInteger.valueOf(11);
		
		assertEquals(target,IdConverter.getFieldSize(int1, int2,100));
	}
	
	@Test
	public void testIdtoFieldAllPositive() {
		
		BigInteger start = BigInteger.valueOf(1000);
		BigInteger end = BigInteger.valueOf(2008);
		BigInteger target =  BigInteger.valueOf(1500);
		BigInteger target1 =  BigInteger.valueOf(1511);
		BigInteger target2 =  BigInteger.valueOf(1001);
		BigInteger target3 =  BigInteger.valueOf(2008);
		
		assertEquals(49 , IdConverter.IDtoField(start, end, target,100));
		assertEquals(51 , IdConverter.IDtoField(start, end, target1,100));
		assertEquals(0 , IdConverter.IDtoField(start, end, target2,100));
		assertEquals(99 , IdConverter.IDtoField(start, end, target3,100));
	}
	
	@Test
	public void testIdtoFieldAllNegative() {
		
		BigInteger start = IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(2000));
		BigInteger end = IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(992));
		BigInteger target =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(1499));
		BigInteger target1 =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(1490));
		BigInteger target2 =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(1999));
		BigInteger target3 =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(993));
		
		assertEquals(50 , IdConverter.IDtoField(start, end, target,100));
		assertEquals(50 , IdConverter.IDtoField(start, end, target1,100));
		assertEquals(0 , IdConverter.IDtoField(start, end, target2,100));
		assertEquals(99 , IdConverter.IDtoField(start, end, target3,100));
	}
	
	@Test
	public void testIdtoFieldZeroCrossing() {
		
		BigInteger start = IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(300));
		BigInteger end = BigInteger.valueOf(100);
		BigInteger target =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(299));
		BigInteger target1 =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(296));
		BigInteger target2 =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(295));
		BigInteger target3 =  BigInteger.valueOf(100);
		BigInteger target4 =  BigInteger.valueOf(95);
		BigInteger target5 =  BigInteger.valueOf(96);
		BigInteger target6 =  BigInteger.valueOf(92);
		BigInteger target7 =  BigInteger.valueOf(91);
		
		
		assertEquals(0 , IdConverter.IDtoField(start, end, target,100));
		assertEquals(0 , IdConverter.IDtoField(start, end, target1,100));
		assertEquals(1 , IdConverter.IDtoField(start, end, target2,100));
		assertEquals(99 , IdConverter.IDtoField(start, end, target3,100));
		assertEquals(98 , IdConverter.IDtoField(start, end, target4,100));
		assertEquals(99 , IdConverter.IDtoField(start, end, target5,100));
		assertEquals(98 , IdConverter.IDtoField(start, end, target6,100));
		assertEquals(97 , IdConverter.IDtoField(start, end, target7,100));
		assertEquals(-1 , IdConverter.IDtoField(start, end, start,100));
	}
	
	@Test
	public void testFieldToIDAllPositive() {
		
		BigInteger start = BigInteger.valueOf(1000);
		BigInteger end = BigInteger.valueOf(2008);
		BigInteger target =  BigInteger.valueOf(1506);
		BigInteger target1 =  BigInteger.valueOf(1516);
		BigInteger target2 =  BigInteger.valueOf(1006);
		BigInteger target3 =  BigInteger.valueOf(1996);
		
		assertEquals(target , IdConverter.FieldToId(end, start, 50,100));
		assertEquals(target1 ,IdConverter.FieldToId(end, start, 51,100));
		assertEquals(target2 ,IdConverter.FieldToId(end, start, 0,100));
		assertEquals(target3 , IdConverter.FieldToId(end, start, 99,100));
	}
	
	@Test
	public void testFieldToIDZeroCrossing() {
		
		BigInteger start = IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(300));
		BigInteger end = BigInteger.valueOf(103);
		BigInteger target =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(297));
		BigInteger target1 =  IdConverter.maxIDBigInt.subtract(BigInteger.valueOf(293));
		BigInteger target2 =  BigInteger.valueOf(99);
		BigInteger target3 =  BigInteger.valueOf(95);
		BigInteger target4 =  BigInteger.valueOf(91);
		
		
		assertEquals(target , IdConverter.FieldToId(end, start, 0,100));
		assertEquals(target1 ,IdConverter.FieldToId(end, start, 1,100));
		assertEquals(target2 ,IdConverter.FieldToId(end, start, 99,100));
		assertEquals(target3 , IdConverter.FieldToId(end, start, 98,100));
		assertEquals(target4 , IdConverter.FieldToId(end, start, 97,100));
		
	}
	
	@Test
	public void testTryByteArray() {
		byte[] b = new byte[] { (byte) 0x00, (byte) 0xff, (byte) 0x01};
		assertEquals(0,b[0] );
		assertEquals(-1,b[1]);
		assertEquals(1,b[2]);
		assertEquals(21, IdConverter.maxIDBigInt.toByteArray().length);
	}

}
