package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;


/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCond_ArrStr.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 *  create table AMS_FilterCond_ArrStr
 *  (
 *  iFilterConditionRef	INT NOT NULL,
 *  cKeyValue		VARCHAR(16),
 *  sOperator		SMALLINT
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_FilterCond_ArrStr")
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName="iFilterConditionID")
public class StringArrayFilterConditionDTO extends FilterConditionDTO implements HasManuallyJoinedElements {

	/**
	 * Die Compare-Values. Werden manuell zugeordnet.
	 */
	@Transient 
	private List<StringArrayFilterConditionCompareValuesDTO> compareValues = new LinkedList<StringArrayFilterConditionCompareValuesDTO>();
	
	@Column(name = "cKeyValue", length = 16)
	private String keyValue;

	@Column(name = "sOperator")
	private short operator;
	
	

	/**
	 * @return the keyValue
	 */
	@SuppressWarnings("unused")
	private String getKeyValue() {
		return keyValue;
	}

	public MessageKeyEnum getKeyValueEnum() {
		return MessageKeyEnum.getEnumFor(keyValue);
	}
	
	/**
	 * @param keyValue
	 *            the keyValue to set
	 */
	@SuppressWarnings("unused")
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	/**
	 * @return the operator
	 */
	@SuppressWarnings("unused")
	private short getOperator() {
		return operator;
	}

	public StringRegelOperator getOperatorEnum(){
		return StringRegelOperator.valueOf(operator);
	}
	
	/**
	 * @param operator
	 *            the operator to set
	 */
	@SuppressWarnings("unused")
	public void setOperator(short operator) {
		this.operator = operator;
	}

	public List<StringArrayFilterConditionCompareValuesDTO> getCompareValueList(){
		return new LinkedList<StringArrayFilterConditionCompareValuesDTO>(compareValues);
	}

	/**
	 * @param compareValues the compareValues to set
	 */
	@SuppressWarnings("unused")
	public void setCompareValues(List<StringArrayFilterConditionCompareValuesDTO> compareValues) {
		this.compareValues = compareValues;
	}

	@Override
	public String toString() {
//		final StringBuilder resultBuilder = new StringBuilder(this.getClass().getSimpleName());
		final StringBuilder resultBuilder = new StringBuilder(super.toString());

		resultBuilder.append(": ");
		resultBuilder.append(compareValues.toString());
		resultBuilder.append(", ");
		resultBuilder.append(this.getKeyValue());
		resultBuilder.append(", ");
		resultBuilder.append(this.getOperatorEnum());
		return resultBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compareValues == null) ? 0 : compareValues.hashCode());
		result = prime * result
				+ ((keyValue == null) ? 0 : keyValue.hashCode());
		result = prime * result + operator;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StringArrayFilterConditionDTO))
			return false;
		final StringArrayFilterConditionDTO other = (StringArrayFilterConditionDTO) obj;
		if (compareValues == null) {
			if (other.compareValues != null)
				return false;
		} else if (!compareValues.equals(other.compareValues))
			return false;
		if (keyValue == null) {
			if (other.keyValue != null)
				return false;
		} else if (!keyValue.equals(other.keyValue))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}
	public void setOperatorEnum(StringRegelOperator op){
		operator = op.databaseValue();
	}

	public void setKeyValue(MessageKeyEnum keyValue2) {
		keyValue = keyValue2.getStringValue();
	}

	public List<String> getCompareValueStringList() {
		List<String> list = new LinkedList<String>();
		for (StringArrayFilterConditionCompareValuesDTO value : getCompareValueList()) {
			list.add(value.getCompValue());
		}
		return list;
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		// TODO CLEAN UP
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<StringArrayFilterConditionCompareValuesDTO> alleVergleichswerte = mapper.loadAll(StringArrayFilterConditionCompareValuesDTO.class, true);
		
		this.compareValues.clear();
		
		for (StringArrayFilterConditionCompareValuesDTO vergleichswert : alleVergleichswerte) {
			if( vergleichswert.getFilterConditionRef() == this.getIFilterConditionID() ) {
				this.compareValues.add(vergleichswert);
			}
		}
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
		
	}
	
	
}
