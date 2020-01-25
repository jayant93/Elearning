package com.ideyatech.opentides.um.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * 
 * @author morjo
 *
 */
public final class BeanUtil {

	private static final Logger LOG = LoggerFactory.getLogger(BeanUtil.class);

	private BeanUtil() {
		// prevent init
	}

	/**
	 * Wrapper for exception handling.
	 * 
	 * @param dest
	 * @param origin
	 */
	public static void copyProperties(Object src, Object dest) {

		try {
			BeanUtils.copyProperties(src, dest);
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}
	}

	public static <T, K> List<T> copyPropertiesToList(Class<T> destClass, List<K> srcList) {

		List<T> result = new ArrayList<>();

		for (K k : srcList) {
			try {
				// reflection
				T t = destClass.newInstance();
				copyProperties(k, t);
				result.add(t);

			} catch (Exception ex) {
				LOG.error(ex.getMessage());
			}
		}
		return result;
	}

	public static <T, K> T copyPropertiesToObj(Class<T> destClass, K origin) {
		T result = null;

		try {
			// reflection
			result = destClass.newInstance();
			copyProperties(origin, result);

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}

		return result;
	}
}