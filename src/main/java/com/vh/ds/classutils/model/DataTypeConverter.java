package com.vh.ds.classutils.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DataTypeConverter {

	public static Object convert(String data, DataType dataType) {
		if(dataType == null) {
			return data;
		}

		Object dataObject = null;

		switch(dataType) {
			case BOOLEAN:
				if(!isBoolean(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Boolean.valueOf(data);
				break;
			case BYTE:
				if(!isByte(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Byte.valueOf(data);
				break;
			case CHAR:
				if(!isChar(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Character.valueOf(data.toCharArray()[0]);
				break;
			case DATE:
				if(!isDate(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				try {
					dataObject = new SimpleDateFormat().parse(data);
				}
				catch(ParseException e) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				break;
			case DOUBLE:
				if(!isDouble(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Double.valueOf(data);
				break;
			case FLOAT:
				if(!isFloat(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Float.valueOf(data);
				break;
			case INTEGER:
				if(!isInteger(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Integer.valueOf(data);
				break;
			case LONG:
				if(!isLong(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Long.valueOf(data);
				break;
			case SHORT:
				if(!isShort(data)) {
					throw new IllegalArgumentException("La donnée \"" + data + "\" ne peut être convertie en objet du type " + dataType);
				}
				dataObject = Short.valueOf(data);
				break;
			case STRING:
				dataObject = data;
				break;
			default:
				throw new IllegalStateException("Le type d'objet n'est pas pris en compte");
		}

		return dataObject;
	}

	/**
	 * Tente d'identifier le type d'une cellule à partir de son contenu et du type de données déjà identifié.
	 * PEUT ETRE NULL pour les cellules vides et non encore identifiées
	 */
	public static DataType getCellDataType(String currentCell, DataType currentType) {
		if(currentType != null) {
			currentType = getCompatibleDataType(currentCell, currentType);
		} else {
			currentType = getPossibleDataType(currentCell);
		}

		return currentType;
	}

	/**
	 * Tente d'identifier le type d'une cellule à partir de son contenu
	 */
	private static DataType getPossibleDataType(String currentCell) {
		// Impossible d'identifier le type pour l'instant.
		if(currentCell == null) {
			throw new IllegalStateException("Aucune donnée !");
		}

		currentCell = currentCell.trim();

		// Impossible d'identifier le type pour l'instant.
		if(currentCell.trim().isEmpty()) {
			return null;
		}

		// L'ordre a une importance : du plus "précis" au plus "général".

		if(isBoolean(currentCell)) {
			return DataType.BOOLEAN;
		}

		// Nombres entiers
		if(isByte(currentCell)) {
			return DataType.BYTE;
		}
		if(isShort(currentCell)) {
			return DataType.SHORT;
		}
		if(isInteger(currentCell)) {
			return DataType.INTEGER;
		}
		if(isLong(currentCell)) {
			return DataType.LONG;
		}

		// Nombres à virgule
		if(isFloat(currentCell)) {
			return DataType.FLOAT;
		}
		if(isDouble(currentCell)) {
			return DataType.DOUBLE;
		}

		// Dates
		if(isDate(currentCell)) {
			return DataType.DATE;
		}

		// Caractère
		if(isChar(currentCell)) {
			return DataType.CHAR;
		}

		// Par défaut, c'est une chaîne de caractères.
		return DataType.STRING;
	}

	private static DataType getCompatibleDataType(String currentCell, DataType currentType) {
		if(currentType == null) {
			throw new IllegalArgumentException("Aucun type de données fourni");
		}

		if(currentCell == null || currentCell.trim().isEmpty()) {
			return currentType;
		}

		currentCell = currentCell.trim();

		switch(currentType) {
			case STRING:
				// tout est compatible avec ce format.
				break;

			case BOOLEAN:
				if(!isBoolean(currentCell)) {
					currentType = DataType.STRING;
				}
				break;

			case CHAR:
				if(!isChar(currentCell)) {
					currentType = DataType.STRING;
				}
				break;

			case DATE:
				if(!isDate(currentCell)) {
					currentType = DataType.STRING;
				}
				break;

			case BYTE:
				if(isByte(currentCell)) {
					break;
				}
				// Si ce n'est pas un BYTE, c'est peut-être un SHORT
				currentType = DataType.SHORT;
				//$FALL-THROUGH$
			case SHORT:
				if(isShort(currentCell)) {
					break;
				}
				// Si ce n'est pas un SHORT, c'est peut-être un INTEGER
				currentType = DataType.INTEGER;
				//$FALL-THROUGH$
			case INTEGER:
				if(isInteger(currentCell)) {
					break;
				}
				// Si ce n'est pas un INTEGER, c'est peut-être un LONG
				currentType = DataType.LONG;
				//$FALL-THROUGH$
			case LONG:
				if(!isLong(currentCell)) {
					currentType = DataType.STRING;
				}
				break;

			case FLOAT:
				if(isFloat(currentCell)) {
					break;
				}
				// Si ce n'est pas un FLOAT, c'est peut-être un DOUBLE
				currentType = DataType.DOUBLE;
				//$FALL-THROUGH$
			case DOUBLE:
				if(!isDouble(currentCell)) {
					currentType = DataType.STRING;
				}
				break;

			default:
				throw new IllegalArgumentException("Type de données non géré : " + currentType);
		}

		return currentType;
	}

	private static boolean isShort(String currentCell) {
		boolean isShort = false;

		try {
			Short.valueOf(currentCell);
			isShort = true;
		}
		catch(NumberFormatException pe) {
			//			isShort = true;
		}

		return isShort;
	}

	private static boolean isInteger(String currentCell) {
		boolean isInteger = false;

		try {
			Integer.valueOf(currentCell);
			isInteger = true;
		}
		catch(NumberFormatException pe) {
			//			isInteger = true;
		}

		return isInteger;
	}

	private static boolean isLong(String currentCell) {
		boolean isLong = false;

		try {
			Long.valueOf(currentCell);
			isLong = true;
		}
		catch(NumberFormatException pe) {
			//			isLong = true;
		}

		return isLong;
	}

	private static boolean isFloat(String currentCell) {
		boolean isFloat = false;

		try {
			Float.valueOf(currentCell);
			isFloat = true;
		}
		catch(NumberFormatException pe) {
			//			isFloat = true;
		}

		return isFloat;
	}

	private static boolean isDouble(String currentCell) {
		boolean isDouble = false;

		try {
			Double.valueOf(currentCell);
			isDouble = true;
		}
		catch(NumberFormatException pe) {
			//			isDouble = true;
		}

		return isDouble;
	}

	private static boolean isDate(String currentCell) {
		boolean isDate = false;

		try {
			// Constructs a SimpleDateFormat using the default pattern and date format symbols for the default locale.
			new SimpleDateFormat().parse(currentCell);
			isDate = true;
		}
		catch(ParseException pe) {
			//			isDate = true;
		}

		return isDate;
	}

	private static boolean isChar(String currentCell) {
		boolean isChar = false;

		if(currentCell.length() == 1) {
			isChar = true;
		}

		return isChar;
	}

	private static boolean isByte(String currentCell) {
		boolean isByte = false;

		try {
			Byte.valueOf(currentCell);
			isByte = true;
		}
		catch(NumberFormatException nfe) {
			//			isByte = false;
		}

		return isByte;
	}

	private static boolean isBoolean(String currentCell) {
		boolean isBoolean = false;

		isBoolean |= "true".equalsIgnoreCase(currentCell);
		isBoolean |= "false".equalsIgnoreCase(currentCell);
		//		isBoolean |= "o".equalsIgnoreCase(currentCell);
		//		isBoolean |= "n".equalsIgnoreCase(currentCell);
		//		isBoolean |= "y".equalsIgnoreCase(currentCell);
		//		isBoolean |= "oui".equalsIgnoreCase(currentCell);
		//		isBoolean |= "non".equalsIgnoreCase(currentCell);

		return isBoolean;
	}

}
