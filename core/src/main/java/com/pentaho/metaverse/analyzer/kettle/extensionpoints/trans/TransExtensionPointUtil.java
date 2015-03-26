/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */
package com.pentaho.metaverse.analyzer.kettle.extensionpoints.trans;

import com.pentaho.dictionary.DictionaryConst;
import com.pentaho.metaverse.impl.MetaverseBuilder;
import com.pentaho.metaverse.api.Namespace;
import com.pentaho.metaverse.messages.Messages;
import com.pentaho.metaverse.util.MetaverseUtil;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.trans.TransMeta;
import com.pentaho.metaverse.api.IDocument;
import com.pentaho.metaverse.api.IMetaverseBuilder;
import com.pentaho.metaverse.api.IMetaverseNode;
import com.pentaho.metaverse.api.IMetaverseObjectFactory;
import com.pentaho.metaverse.api.INamespace;
import com.pentaho.metaverse.api.MetaverseException;

import java.net.URLConnection;

/**
 * This class offers helper methods for Transformation Extension Points used by the lineage capability.
 */
public class TransExtensionPointUtil {

  public static void addLineageGraph( final TransMeta transMeta ) throws MetaverseException {

    if ( transMeta == null ) {
      throw new MetaverseException( Messages.getString( "ERROR.Document.IsNull" ) );
    }

    String filename = getFilename( transMeta );

    final Graph graph = new TinkerGraph();
    final IMetaverseBuilder metaverseBuilder = new MetaverseBuilder( graph );
    final IMetaverseObjectFactory objFactory = MetaverseUtil.getDocumentController().getMetaverseObjectFactory();

    // Add the client design node
    final String clientName = KettleClientEnvironment.getInstance().getClient().toString();
    final INamespace namespace = new Namespace( clientName );

    final IMetaverseNode designNode =
      objFactory.createNodeObject( clientName, clientName, DictionaryConst.NODE_TYPE_LOCATOR );
    metaverseBuilder.addNode( designNode );

    // Create a document object containing the transMeta
    final IDocument document = MetaverseUtil.createDocument(
      namespace,
      transMeta,
      filename,
      transMeta.getName(),
      "ktr",
      URLConnection.getFileNameMap().getContentTypeFor( "trans.ktr" )
    );

    MetaverseUtil.addLineageGraph( document, graph );
  }

  public static String getFilename( TransMeta transMeta ) {
    String filename = transMeta.getFilename();
    if ( filename == null ) {
      filename = transMeta.getPathAndName();
    }
    if ( filename == null ) {
      filename = "";
    }
    return filename;
  }
}
