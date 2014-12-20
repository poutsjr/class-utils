package com.vh.ds.classutils.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

public enum DataType {

	INTEGER("integer", "Entier (int)"),
	FLOAT("float", "Nombre à virgule (float)"),
	STRING("string", "Chaine de caractère"),
	LONG("long", "Entier long (long)"),
	DATE("date", "Date"),
	BOOLEAN("boolean", "Booléen"),
	BYTE("byte", "Entier très court (byte)"),
	CHAR("char", "Caractère"),
	SHORT("short", "Entier court (short)"),
	DOUBLE("double", "Chiffre à virgule long (double)");

	private String code;
	private String label;

	private DataType(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String getCode() {
		return code;
	}
	

	public String getLabel() {
		return label;
	}

	private static Map<Class<?>, DataType> classWrapper = new HashMap<Class<?>, DataType>();

	static {
		classWrapper.put(Integer.class, DataType.INTEGER);
		classWrapper.put(int.class, DataType.INTEGER);
		classWrapper.put(Float.class, DataType.FLOAT);
		classWrapper.put(float.class, DataType.FLOAT);
		classWrapper.put(Double.class, DataType.DOUBLE);
		classWrapper.put(double.class, DataType.DOUBLE);
		classWrapper.put(BigDecimal.class, DataType.DOUBLE);
		classWrapper.put(BigInteger.class, DataType.LONG);
		classWrapper.put(String.class, DataType.STRING);
		classWrapper.put(Long.class, DataType.LONG);
		classWrapper.put(long.class, DataType.LONG);
		classWrapper.put(Date.class, DataType.DATE);
		classWrapper.put(Boolean.class, DataType.BOOLEAN);
		classWrapper.put(boolean.class, DataType.BOOLEAN);
		classWrapper.put(Byte.class, DataType.BYTE);
		classWrapper.put(Character.class, DataType.CHAR);
		classWrapper.put(XMLGregorianCalendar.class, DataType.DATE);
		classWrapper.put(char.class, DataType.CHAR);
		classWrapper.put(Short.class, DataType.SHORT);
		classWrapper.put(short.class, DataType.SHORT);
	}

	public static DataType fromType(Class<?> clazz) {
		return classWrapper.get(clazz);
	}

	public static boolean isAssignableFromType(Class<?> clazz) {
		return classWrapper.containsKey(clazz);
	}

	/**
	 * Pour récupérer le DataType dans le front depuis une page, préférer utiliser la valeur de l'enum puis utiliser "valueOf" de l'enum.
	 * @param dataTypeCode
	 * @return le <code>DataType</code> correspondant, <code>null</code> si aucun ne correspond.
	 */
	@Deprecated
	public static DataType fromCode(String dataTypeCode) {
		DataType result = null;

		DataType[] values = DataType.values();
		for(int i = 0; i < values.length; ++i) {
			if(values[i].getCode().equalsIgnoreCase(dataTypeCode)) {
				result = values[i];
				break;
			}
		}

		return result;
	}

}
