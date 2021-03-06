//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/AeSimpleTypeSampleDataProducer.java,v 1.6 2008/03/20 14:27:22 kpease Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata;

import org.activebpel.rt.xml.schema.*;
import org.apache.commons.codec.binary.Base64;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for generating Sample data for each of the Schema built-in simple types.
 */
public class AeSimpleTypeSampleDataProducer {
    private Map<QName, Object> mMap = new HashMap<>();
    private Date mDate = new Date();

    /**
     * Ctor
     */
    public AeSimpleTypeSampleDataProducer() {
        this(new Date());
    }

    /**
     * Constructor.
     *
     * @param aDate
     */
    public AeSimpleTypeSampleDataProducer(Date aDate) {
        setDate(aDate);
        populateMap();
    }

    /**
     * Gets sample data for the given simple type.
     *
     * @param aType
     * @return String sample data string value.
     */
    public String getSampleData(QName aType) {
        Object type = getMap().get(aType);
        if (type == null) {
            return "data"; //$NON-NLS-1$
        } else {
            return type.toString();
        }
    }

    /**
     * Gets sample data for the given simple type, bound by the minimum and maximum value constraints.
     * <p/>
     * For numeric types, the sample value is the midpoint between the minimum and maximum values
     * If only the minimum value is given, returns minimum + 1
     * If only the maximum value is given, returns maximum - 1
     * <p/>
     * If the type is not numeric or not constrained, the standard default for the type is returned.
     *
     * @param aType
     * @param aMinInclusive
     * @param aMaxInclusive
     * @param aMinExclusive
     * @param aMaxExclusive
     * @return String sample data string value.
     */
    public String getSampleData(QName aType, String aMinInclusive, String aMaxInclusive, String aMinExclusive, String aMaxExclusive) {
        Object type = getMap().get(aType);
        if (type == null) {
            return "data"; //$NON-NLS-1$
        } else {
            try {
                Constructor ctor = type.getClass().getConstructor(new Class[]{String.class});
                if (ctor != null && type instanceof Number) {
                    Number typeMin = null;
                    Number typeMax = null;

                    // If we have both a min inclusive and min exclusive, we'll pick the
                    // larger of the two
                    if (aMinInclusive != null && aMinExclusive != null) {
                        Number typeMinInc = (Number) ctor.newInstance(aMinInclusive);
                        Number typeMinEx = (Number) ctor.newInstance(aMinExclusive);
                        if (typeMinInc.doubleValue() > typeMinEx.doubleValue()) {
                            typeMin = typeMinInc;
                            aMinExclusive = null;
                        } else {
                            typeMin = typeMinEx;
                            aMinInclusive = null;
                        }
                    } else if (aMinInclusive != null) {
                        typeMin = (Number) ctor.newInstance(aMinInclusive);
                    } else if (aMinExclusive != null) {
                        typeMin = (Number) ctor.newInstance(aMinExclusive);
                    }

                    if (aMaxInclusive != null && aMaxExclusive != null) {
                        // pick the smaller of the two
                        Number typeMaxInc = (Number) ctor.newInstance(aMaxInclusive);
                        Number typeMaxEx = (Number) ctor.newInstance(aMaxExclusive);
                        if (typeMaxInc.doubleValue() < typeMaxEx.doubleValue()) {
                            typeMax = typeMaxInc;
                            aMaxExclusive = null;
                        } else {
                            typeMax = typeMaxEx;
                            aMaxInclusive = null;
                        }
                    } else if (aMaxInclusive != null)
                        typeMax = (Number) ctor.newInstance(aMaxInclusive);
                    else if (aMaxExclusive != null)
                        typeMax = (Number) ctor.newInstance(aMaxExclusive);

                    // value will be the midpoint if both min and max are given
                    if (typeMin != null && typeMax != null) {
                        double midpoint = typeMin.doubleValue() + (typeMax.doubleValue() - typeMin.doubleValue()) / 2;
                        long midpointNoDecimal = (long) midpoint;

                        // use the midpoint with no decimal whenever possible
                        if (((aMinInclusive != null && midpointNoDecimal >= typeMin.doubleValue()) ||
                                (aMinExclusive != null && midpointNoDecimal > typeMin.doubleValue())) &&
                                ((aMaxInclusive != null && midpointNoDecimal <= typeMax.doubleValue()) ||
                                        (aMaxExclusive != null && midpointNoDecimal < typeMax.doubleValue()))) {
                            type = ctor.newInstance(String.valueOf(midpointNoDecimal));
                        } else {
                            type = ctor.newInstance(String.valueOf(midpoint));
                        }
                    } else if (typeMin != null) {
                        long value = typeMin.longValue() + 1;
                        type = ctor.newInstance(String.valueOf(value));
                    } else if (typeMax != null) {
                        long value = typeMax.longValue() - 1;
                        type = ctor.newInstance(String.valueOf(value));
                    }
                }
            } catch (Exception ex) {
                // we'll eat this and it'll just return the default value
            }
            return type.toString();
        }
    }

    /**
     * Populated the data map for each sample data type.
     */
    protected void populateMap() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDate());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Map<QName, Object> map = getMap();
        map.put(AeTypeMapping.XSD_ANYURI, new AeSchemaAnyURI("anyURI")); //$NON-NLS-1$
        map.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType"), "anyType"); //$NON-NLS-1$ //$NON-NLS-2$
        map.put(AeTypeMapping.XSD_BASE64_BINARY, new AeSchemaBase64Binary(Base64.encodeBase64String("base64-string".getBytes()))); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_BOOLEAN, Boolean.TRUE);
        map.put(AeTypeMapping.XSD_BYTE, (byte) 1);
        map.put(AeTypeMapping.XSD_DATE, new AeSchemaDate(getDate()));
        map.put(AeTypeMapping.XSD_DATETIME, new AeSchemaDateTime(getDate()));
        map.put(AeTypeMapping.XSD_DAY, new AeSchemaDay(day, 0));
        map.put(AeTypeMapping.XSD_DECIMAL, 1);
        map.put(AeTypeMapping.XSD_DOUBLE, (double) 1);
        map.put(AeTypeMapping.XSD_DURATION, new AeSchemaDuration());
        map.put(AeTypeMapping.XSD_FLOAT, (float) 1);
        map.put(AeTypeMapping.XSD_HEX_BINARY, new AeSchemaHexBinary("10203F")); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_INT, 1);
        map.put(AeTypeMapping.XSD_INTEGER, 1);
        map.put(AeTypeMapping.XSD_LONG, (long) 1);
        map.put(AeTypeMapping.XSD_MONTH, new AeSchemaMonth(month, 0));
        map.put(AeTypeMapping.XSD_MONTHDAY, new AeSchemaMonthDay(month, day, 0));
        map.put(AeTypeMapping.XSD_QNAME, "qname"); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_SHORT, (short) 1);
        map.put(AeTypeMapping.XSD_STRING, "string"); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_TIME, new AeSchemaTime(getDate()));
        map.put(AeTypeMapping.XSD_YEAR, new AeSchemaYear(year, 0));
        map.put(AeTypeMapping.XSD_YEARMONTH, new AeSchemaYearMonth(year, month, 0));
        map.put(AeTypeMapping.XSD_POSITIVE_INTEGER, 1);
        map.put(AeTypeMapping.XSD_NORMALIZED_STRING, "string"); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_TOKEN, "string");  //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_UNSIGNED_BYTE, 1);
        map.put(AeTypeMapping.XSD_NEGATIVE_INTEGER, -1);
        map.put(AeTypeMapping.XSD_NON_NEGATIVE_INTEGER, 1);
        map.put(AeTypeMapping.XSD_NON_POSITIVE_INTEGER, -1);
        map.put(AeTypeMapping.XSD_UNSIGNED_INT, 1);
        map.put(AeTypeMapping.XSD_UNSIGNED_LONG, (long) 1);
        map.put(AeTypeMapping.XSD_UNSIGNED_SHORT, (short) 1);
        map.put(AeTypeMapping.XSD_NAME, "name");   //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_NCNAME, "ncname"); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_LANGUAGE, "en");   //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_ID, "id");     //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_IDREF, "idref");    //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_IDREFS, "idrefs");   //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_ENTITY, "entity");   //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_ENTITIES, "entities"); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_NOTATION, "notation"); //$NON-NLS-1$
        map.put(AeTypeMapping.XSD_NMTOKENS, "nmtokens"); //$NON-NLS-1$
    }

    /**
     * @return the map
     */
    protected Map<QName, Object> getMap() {
        return mMap;
    }

    /**
     * @param aMap the map to set
     */
    protected void setMap(Map<QName, Object> aMap) {
        mMap = aMap;
    }

    protected Date getDate() {
        return mDate;
    }

    protected void setDate(Date aDate) {
        mDate = aDate;
    }

}
 
