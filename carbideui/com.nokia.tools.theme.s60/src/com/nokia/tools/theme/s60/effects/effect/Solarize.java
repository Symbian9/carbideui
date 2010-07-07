/*
* Copyright (c) 2006-2010 Nokia Corporation and/or its subsidiary(-ies). 
* All rights reserved.
* This component and the accompanying materials are made available
* under the terms of "Eclipse Public License v1.0"
* which accompanies this distribution, and is available
* at the URL "http://www.eclipse.org/legal/epl-v10.html".
*
* Initial Contributors:
* Nokia Corporation - initial contribution.
*
* Contributors:
*
* Description:
*
*/
/*
 * To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
package com.nokia.tools.theme.s60.effects.effect;

import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.nokia.tools.media.image.CoreImage;
import com.nokia.tools.platform.theme.EffectConstants;
import com.nokia.tools.platform.theme.LayerEffect;
import com.nokia.tools.platform.theme.ParameterModel;
import com.nokia.tools.platform.theme.ThemeTag;
import com.nokia.tools.theme.s60.effects.EffectObject;
import com.nokia.tools.theme.s60.effects.ImageProcessor;
import com.nokia.tools.theme.s60.siscreation.MorphingItemDefinition;

/**
 * To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Solarize implements ImageProcessor {

	private final String effectUID = "0x10204AE7";
	
	private HashMap<String, String> map1 = new HashMap<String, String>();

	public RenderedImage doProcessing(RenderedImage src, HashMap map) {
		int bitmask = 7;
		int threshold = 127;
		int blendfactor = 255;
		String inputA = (String) getEffectProperties().get("INPUTA");
		if (src == null)
			return src;

		CoreImage main = CoreImage.create(src);
		CoreImage mask = null;
		if (main.getNumBands() == 4 && inputA.equalsIgnoreCase("RGB")) {
			mask = main.copy().extractMask();
			main.reduceToThreeBand();
		}
		EffectObject eObj = null;
		try {

			if (map.get(EffectConstants.EFFECTOBJECT) instanceof EffectObject) {
				eObj = (EffectObject) map.get(EffectConstants.EFFECTOBJECT);
				if (eObj == null)
					eObj = EffectObject.getEffect(EffectConstants.SOLARIZE);
				bitmask = new Integer((String) eObj
						.getAttributeValue(EffectConstants.BITMASK)).intValue();
				threshold = new Integer((String) eObj
						.getAttributeValue(EffectConstants.ATTR_THRESHOLD))
						.intValue();
				blendfactor = new Integer((String) eObj
						.getAttributeValue(EffectConstants.BLENDFACTOR))
						.intValue();
			} else {
				LayerEffect le = (LayerEffect) map
						.get(EffectConstants.EFFECTOBJECT);
				if (le.getAttribute(EffectConstants.BITMASK) != null)
					bitmask = new Integer((String) le
							.getAttribute(EffectConstants.BITMASK)).intValue();
				if (le.getAttribute(EffectConstants.ATTR_THRESHOLD) != null)
					threshold = new Integer((String) le
							.getAttribute(EffectConstants.ATTR_THRESHOLD))
							.intValue();
				if (le.getAttribute(EffectConstants.BLENDFACTOR) != null)
					blendfactor = new Integer((String) le
							.getAttribute(EffectConstants.BLENDFACTOR))
							.intValue();

			}
		} catch (Exception e) {
			e.printStackTrace();
		
			if (eObj != null) {
				eObj.invalidData = true;
			}

			return src;

		}
		CoreImage image = main.copy();
		SolarizeImage(image, bitmask, threshold);
		image = main.copy().composite(image, blendfactor);
		String output = (String) getEffectProperties().get(OUTPUT);
		if (mask != null && output.equalsIgnoreCase("RGBA"))
			image.bandMerge(mask);
		return image.getAwt();

	}

	private static void SolarizeImage(CoreImage src, int factor, int threshold) {
		byte[][] lut = new byte[3][256];
		int red = 1;
		int green = 2;
		int blue = 4;
		for (int i = 0; i < 256; i++) {
			if (i >= threshold) {
				if ((factor & red) != 0)
					lut[0][i] = (byte) (255 - i);
				else
					lut[0][i] = (byte) (i);
				if ((factor & green) != 0)
					lut[1][i] = (byte) (255 - i);
				else
					lut[1][i] = (byte) (i);
				if ((factor & blue) != 0)
					lut[2][i] = (byte) (255 - i);
				else
					lut[2][i] = (byte) (i);

			} else {
				lut[0][i] = (byte) (i);
				lut[1][i] = (byte) (i);
				lut[2][i] = (byte) (i);
			}
		}
		src.colorize(lut);
	}

	public HashMap getOutputParams(Map uiMap, Map attrMap, String type) {

		EffectObject eObj = EffectObject.getEffect(EffectConstants.SOLARIZE);
		HashMap eMap = eObj.getOutputAttributeMap();
		Set set = uiMap.keySet();
		Iterator iter = set.iterator();
		String valueRef = null;
		ParameterModel pm = null;
		while (iter.hasNext()) {
			String name = (String) iter.next();
			String value = "";
			if (uiMap.get(name) instanceof String)
				value = (String) uiMap.get(name);
			else if (uiMap.get(name) instanceof ParameterModel) {
				pm = (ParameterModel) uiMap.get(name);
				if (pm.isAnimatedModel() && type.equals(ANIMATION))
					valueRef = pm.getAttribute(ThemeTag.ELEMENT_VALUEMODEL_REF);
				else
					value = pm.getValue(name);
			}
			HashMap<Object, Object> map1 = (HashMap) eMap.get(name);
			if (map1 != null) {
				if (valueRef != null) {
					map1.put(ThemeTag.ELEMENT_VALUEMODEL_REF, new String(
							valueRef));
					if (pm.getValue(name) != null)
						map1.put(EffectConstants.ATTR_DEFAULTVALUE, pm
								.getValue(name));
					valueRef = null;
				} else {
					map1.remove(ThemeTag.ELEMENT_VALUEMODEL_REF);
					map1.put(EffectConstants.ATTR_VALUE, value);
				}
			}
		}

		return eMap;
	}

	public HashMap getEffectParameters(Map uiMap, Map attrMap, String type) {
		return getOutputParams(uiMap, attrMap, type);
	}

	public HashMap getEffectProperties() {
		map1.put("INPUTA", "RGB");
		map1.put("INPUTB", "none");
		map1.put("OUTPUT", "RGBA");
		return map1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nokia.tools.theme.s60.effects.ImageProcessor#getEffectString(int,
	 *      int, java.util.HashMap)
	 */
	public StringBuffer getEffectString(int prevLayerPosition,
			int currentPosition, Map effectValues) {

		if (effectValues == null || effectValues.size() <= 0)
			return null;
		boolean morphed = false;
		if (effectValues.get(MORPHED) != null) {
			morphed = new Boolean((String) effectValues.get("MORPHED"))
					.booleanValue();
			effectValues.remove(MORPHED);
		}
		StringBuffer effectStr = new StringBuffer();
		String inputA = "" + currentPosition + FSLASH + RGB;
		String inputB = "none";
		Boolean writeBackToInputLayer = ((Boolean) effectValues
				.get(ThemeTag.KEY_OVERWRITE_INPUT));
		int outputPosition = (writeBackToInputLayer != null && writeBackToInputLayer
				.booleanValue()) ? currentPosition + 1 : currentPosition;
		String output = "" + outputPosition + FSLASH + RGB;

		
		if (morphed)
			effectStr.append(COMMAND).append(SPACE);
		else
			effectStr.append(EFFECT).append(SPACE);
		effectStr.append(UID_STR).append(EQUAL).append(effectUID).append(SPACE);
		effectStr.append(INPUTA).append(EQUAL).append(inputA).append(SPACE);
		effectStr.append(INPUTB).append(EQUAL).append(inputB).append(SPACE);
		effectStr.append(OUTPUT).append(EQUAL).append(output).append(NL);

		
		Map outputParametersMap = getOutputParams(effectValues, null,
				(morphed) ? ANIMATION : SCALEABLEITEM);
		Map[] effectOutputData = (HashMap[]) outputParametersMap.values()
				.toArray(new HashMap[1]);
		for (int i = 0; i < effectOutputData.length; i++) {
			Map nextParam = effectOutputData[i];
			String paramType = (String) nextParam
					.get(EffectConstants.ATTR_TYPE);
			String paramName = (String) nextParam
					.get(EffectConstants.ATTR_NAME);
			String paramValue = (String) nextParam
					.get(EffectConstants.ATTR_VALUE);
			String valueModelRef = (String) nextParam
					.get(ThemeTag.ELEMENT_VALUEMODEL_REF);
			
			if ((paramValue == null || paramValue.trim().length() == 0)
					&& (valueModelRef == null || valueModelRef.trim().length() == 0))
				continue;

			if (morphed && valueModelRef != null) {
				effectStr.append(TAB).append(TAB);
				effectStr.append(NAMEDREF).append(SPACE);
				effectStr.append(paramName).append(SPACE);
				effectStr.append(VALUEID).append(EQUAL);
				Map valueModelMap = (Map) effectValues
						.get(ThemeTag.KEY_VALUE_MODEL_DATA);
				String s = new Integer(MorphingItemDefinition.getValueModelId(
						valueModelRef, valueModelMap)).toString();
				effectStr.append(s).append(NL);

			

			} else {
				effectStr.append(TAB).append(TAB);
				effectStr.append(paramType).append(SPACE);
				effectStr.append(paramName).append(SPACE);
				effectStr.append(paramValue).append(NL);
			}
		}

		effectStr.append(TAB).append(END);

		return effectStr;
	}

}
