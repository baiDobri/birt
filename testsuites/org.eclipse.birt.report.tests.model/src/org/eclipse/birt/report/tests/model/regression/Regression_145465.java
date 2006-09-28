/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Actuate Corporation -
 * initial API and implementation
 ******************************************************************************/

package org.eclipse.birt.report.tests.model.regression;

import java.io.File;
import java.io.IOException;

import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.DesignFileException;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.LibraryHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.tests.model.BaseTestCase;

import com.ibm.icu.util.ULocale;

/**
 * <b>Regression description:</b>
 * <p>
 * NPE is thrown out when deleting the included library in resource folder
 * <p>
 * Steps to reproduce:
 * <ol>
 * <li>Add a label in a library, publish the library
 * <li>New a report, extends lib.label
 * <li>Delete the library in resource folder
 * <li>Refresh the report
 * </ol>
 * <p>
 * <b>Expected result:</b>
 * <p>
 * lib.label can't find its parent, but no exception
 * <p>
 * <b>Actual result:</b>
 * <p>
 * java.lang.NullPointerException at
 * org.eclipse.birt.report.model.api.command.LibraryReloadedEvent.<init>(LibraryReloadedEvent.java:39)
 * <b>Test description:</b>
 * <p>
 * Follow the steps, remove the library and reload the libraries from design,
 * make sure no exception throwed.
 * <p>
 */
public class Regression_145465 extends BaseTestCase
{

	private final static String REPORT = "regression_145465.xml"; //$NON-NLS-1$
	private final static String LIB = "regression_145465_lib.xml"; //$NON-NLS-1$

	/**
	 * @throws IOException
	 * @throws DesignFileException
	 * @throws SemanticException
	 */
	public void test_regression_145465( ) throws IOException,
			DesignFileException, SemanticException
	{
		/*
		 * copyFile( this.getClassFolder( ) + INPUT_FOLDER + REPORT, this
		 * .getClassFolder( ) + OUTPUT_FOLDER + REPORT );
		 */

		copyFile( this.getClassFolder( ) + INPUT_FOLDER + LIB, this
				.getClassFolder( )
				+ OUTPUT_FOLDER + LIB );

		DesignEngine engine = new DesignEngine( new DesignConfig( ) );
		SessionHandle session = engine.newSessionHandle( ULocale.ENGLISH );
		session.setResourceFolder( this.getClassFolder( ) + OUTPUT_FOLDER );

		ReportDesignHandle designHandle = session.openDesign( this
				.getClassFolder( )
				+ INPUT_FOLDER + REPORT );
		LibraryHandle libHandle = designHandle
				.getLibrary( "regression_145465_lib" ); //$NON-NLS-1$
		LabelHandle label = (LabelHandle) libHandle.findElement( "NewLabel" ); //$NON-NLS-1$

		ElementFactory factory = designHandle.getElementFactory( );
		LabelHandle extendsLabel = (LabelHandle) factory.newElementFrom(
				label,
				"extendsLabel" ); //$NON-NLS-1$

		designHandle.getBody( ).add( extendsLabel );
		designHandle.save( );

		// remove the library from resource folder.

		boolean deleted = new File( this.getClassFolder( ) + OUTPUT_FOLDER
				+ LIB ).delete( );
		assertTrue( deleted );

		// refresh the report, make sure no exception is throwed out.

		designHandle.reloadLibraries( );
	}
}
