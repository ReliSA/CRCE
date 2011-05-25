/**
 * This is CoSi Framework and Toolkit – an obsessively black-box component model
 * in OSGi style.
 *
 * Copyright @ 2007-2009 Department of Computer Science and Engineering,
 * University of West Bohemia, Pilsen, CZ
 *
 * This software and this file is available under the Creative Commons
 * Attribution-Noncommercial-Share Alike license. You may obtain a copy
 * of the License at http://creativecommons.org/licenses/ .
 *
 * This software is provided on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations.
 */

package cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc;

/**
 * 
 * Class representing interval in Extra Functional properties.
 * 
 * Interval can be from -infinity to hard value, from hard value to infinity (
 * <-inf,X>,<X,inf> ) or from hard value to hard value (<X,Y>). You can also
 * save average value in interval (<X,avg,Y>). Average value must be higher than
 * X, lower than Y.
 * 
 * 
 * 
 * @author Vojtech Liska
 * 
 */

public class ExtraFuncInterval extends ExtraFuncType {

	private ExtraFuncSimpleType floorValue;
	private ExtraFuncSimpleType ceilingValue;
	private ExtraFuncSimpleType averageValue;
	private boolean includingFloor;
	private boolean includingCeiling;
	private String valueType;

	/**
	 * Constructor - only 2 values specified.
	 */
	public ExtraFuncInterval(ExtraFuncSimpleType P_floorValue,
			boolean P_includingFloor, ExtraFuncSimpleType P_ceilingValue,
			boolean P_includingCeiling, String Ps_type) {

		floorValue = P_floorValue;

		ceilingValue = P_ceilingValue;

		includingFloor = P_includingFloor;

		includingCeiling = P_includingCeiling;

		setValueType(Ps_type);

		check();
	}

	public ExtraFuncInterval(Number P_floorValue, boolean P_includingFloor,
			Number P_ceilingValue, boolean P_includingCeiling, String Ps_type) {

		floorValue = P_floorValue != null ? new ExtraFuncSimpleType(
				P_floorValue) : null;

		ceilingValue = P_ceilingValue != null ? new ExtraFuncSimpleType(
				P_ceilingValue) : null;
		;

		includingFloor = P_includingFloor;

		includingCeiling = P_includingCeiling;

		setValueType(Ps_type);

		check();
	}

	public ExtraFuncInterval(ExtraFuncSimpleType P_floorValue,
			boolean P_includingFloor, ExtraFuncSimpleType P_averageValue,
			ExtraFuncSimpleType P_ceilingValue, boolean P_includingCeiling,
			String Ps_type) {

		floorValue = P_floorValue;

		ceilingValue = P_ceilingValue;

		averageValue = P_averageValue;

		includingFloor = P_includingFloor;

		includingCeiling = P_includingCeiling;

		setValueType(Ps_type);

		check();
	}

	public ExtraFuncInterval(Number P_floorValue, boolean P_includingFloor,
			Number P_averageValue, Number P_ceilingValue,
			boolean P_includingCeiling, String Ps_type) {

		floorValue = P_floorValue != null ? new ExtraFuncSimpleType(
				P_floorValue) : null;

		ceilingValue = P_ceilingValue != null ? new ExtraFuncSimpleType(
				P_ceilingValue) : null;
		;

		averageValue = P_averageValue != null ? new ExtraFuncSimpleType(
				P_averageValue) : null;
		;

		includingFloor = P_includingFloor;

		includingCeiling = P_includingCeiling;

		setValueType(Ps_type);

		check();
	}

	/**
	 * Checks if interval P_value fits in interval range (is part of this
	 * interval).
	 * 
	 * @param P_value
	 *            value to check
	 * 
	 * @return true if P_value is part of this interval, otherwise false
	 */
	public boolean fitsWithinRange(ExtraFuncInterval P_value) {

		if (P_value == null)
			return false;

		if (floorValue == null) {
			if (P_value.getFloorValue() != null)
				return false;
		}

		// (X,infinity) can't match (X,value)
		if (ceilingValue == null) {
			if (P_value.getCeilingValue() != null)
				return false;
		}

		return fitsWithinRange(P_value.getFloorValue())
				&& fitsWithinRange(P_value.getCeilingValue());
	}

	/**
	 * Checks if P_value fits in interval range.
	 * 
	 * @param P_value
	 *            value to check
	 * @return true if P_value fits in interval range, otherwise false
	 */
	public boolean fitsWithinRange(ExtraFuncSimpleType P_value) {

		if (P_value.getType().compareTo(valueType) != 0) {
			return false;
		}

		boolean F_retval = true;

		if (P_value == null)
			return false;

		if (floorValue == null && ceilingValue == null)
			throw new RuntimeException(
					"Incorrent interval format: floor value and ceiling value cannot be both null !");

		if (floorValue == null) {
			if (includingCeiling) { // closed interval
				F_retval = F_retval && (P_value.compareTo(ceilingValue) <= 0);
			} else // open interval
			{
				F_retval = F_retval && (P_value.compareTo(ceilingValue) < 0);
			}
			return F_retval;

		} else if (ceilingValue == null) {
			if (includingFloor) { // closed interval
				F_retval = F_retval && (P_value.compareTo(floorValue) >= 0);
			} else {// open interval
				F_retval = F_retval && (P_value.compareTo(floorValue) > 0);
			}
			return F_retval;
		}

		F_retval = true;

		if (includingCeiling)
			F_retval = F_retval && (P_value.compareTo(ceilingValue) <= 0);
		else
			F_retval = F_retval && (P_value.compareTo(ceilingValue) < 0);

		if (includingFloor)
			F_retval = F_retval && (P_value.compareTo(floorValue) >= 0);
		else
			F_retval = F_retval && (P_value.compareTo(floorValue) > 0);

		return F_retval;
	}

	public ExtraFuncSimpleType getFloorValue() {
		return floorValue;
	}

	public void setFloorValue(ExtraFuncSimpleType floorValue) {
		this.floorValue = floorValue;
	}

	public ExtraFuncSimpleType getCeilingValue() {
		return ceilingValue;
	}

	public void setCeilingValue(ExtraFuncSimpleType ceilingValue) {
		this.ceilingValue = ceilingValue;
	}

	public ExtraFuncSimpleType getAverageValue() {
		return averageValue;
	}

	public void setAverageValue(ExtraFuncSimpleType averageValue) {
		this.averageValue = averageValue;
	}

	/**
	 * Forces interval check for correctness. Can be used after some operations
	 * with interval (setting values, etc.).
	 */
	public void forceCheck() {
		check();
	}

	/*
	 * Checks correctness of created interval.
	 */
	private void check() {

		if (valueType == null)
			throw new RuntimeException(
					"Incorrent interval format: value type must be filled in!");

		// if both are null, interval has no meaning
		if (floorValue == null && ceilingValue == null)
			throw new RuntimeException(
					"Incorrent interval format: floor value and ceiling value cannot be both null !");

		// if one is null -> correct, but can't set average Value then
		// one is null means interval is (-infinity:ceilingValue> or
		// <floorvalue;infinity)
		if (floorValue == null || ceilingValue == null) {
			if (averageValue != null)
				throw new RuntimeException(
						"Incorrent interval format: cannot set average value if floor or ceiling value is null !");
			else
				return;
		}

		// check if floor value is lower than ceiling value and if average value
		// is between them
		if (floorValue.compareTo(ceilingValue) > 0)
			throw new RuntimeException(
					"Incorrent interval format: floor value must be lower than ceiling value or same !");

		if (averageValue != null) {
			if (floorValue.compareTo(averageValue) > 0)
				throw new RuntimeException(
						"Incorrent interval format: floor value must be lower than average value or same !");
			if (averageValue.compareTo(ceilingValue) > 0)
				throw new RuntimeException(
						"Incorrent interval format: average value must be lower than ceiling value or same !");
			// check types
			if ((floorValue.getType() != ceilingValue.getType())
					|| (floorValue.getType() != averageValue.getType())) {
				throw new RuntimeException(
						"Incorrent interval types: all values must have the same type !");
			}
		} else {
			if (!(floorValue.getType().equals(ceilingValue.getType()))) {
				throw new RuntimeException(
						"Incorrent interval types: all values must have the same type !");
			}
		}

	}

	public String toString() {

		String leftBracket = (includingFloor ? "[" : "(");
		String rightBracket = (includingCeiling ? "]" : ")");

		if (averageValue == null)
			return leftBracket
					+ ((floorValue == null) ? "-infinity" : floorValue
							.toString())
					+ ","
					+ ((ceilingValue == null) ? "infinity" : ceilingValue
							.toString()) + rightBracket;
		else
			return leftBracket
					+ ((floorValue == null) ? "-infinity" : floorValue
							.toString())
					+ ","
					+ averageValue.toString()
					+ ","
					+ ((ceilingValue == null) ? "infinity" : ceilingValue
							.toString()) + rightBracket;
	}

	@Override
	public String getType() {
		return INTERVAL;
	}

	@Override
	public ExtraFuncInterval getValue() {
		return this;
	}

	/**
	 * Returns type of stored value.
	 * 
	 * @return type of value stored in interval
	 */
	public String getValueType() {
		return this.valueType;
	}

	@Override
	public boolean fulfil(ExtraFuncType P_requiredType) {

		if (P_requiredType.getType().equals(ExtraFuncType.INTERVAL))
			return fitsWithinRange((ExtraFuncInterval) P_requiredType);
		else if (P_requiredType instanceof ExtraFuncSimpleType) {
			return fitsWithinRange((ExtraFuncSimpleType) P_requiredType);
		}

		return false;
	}

	public boolean isIncludingFloor() {
		return includingFloor;
	}

	public void setIncludingFloor(boolean includingFloor) {
		this.includingFloor = includingFloor;
	}

	public boolean isIncludingCeiling() {
		return includingCeiling;
	}

	public void setIncludingCeiling(boolean includingCeiling) {
		this.includingCeiling = includingCeiling;
	}

	public void setValueType(String valueType) {
		if (valueType.length() == 0) {
			this.valueType = floorValue == null ? ceilingValue.getType()
					: floorValue.getType();
		} else
			this.valueType = valueType;

	}

}
