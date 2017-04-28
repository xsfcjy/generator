/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 用于分库分表的路由字段设置
 * 
 * @author sfxie
 * @since 2017-04-28
 *
 */
public class PartitionUtil {
	
	/**
	 * 分库分表的路由字段设置
	 * @param answer
	 * @param introspectedTable
	 * @param sb
	 * @param and
	 */
	public static void addPartitionField(XmlElement answer,
			IntrospectedTable introspectedTable, StringBuilder sb, boolean and) {
		String partitionFieldString = introspectedTable.getTableConfiguration()
				.getPartitionField();
		if (null != partitionFieldString) {
			String[] partitionFields = partitionFieldString.split(",");
			for (String partionField : partitionFields) {
				sb.setLength(0);
				IntrospectedColumn introspectedColumn = introspectedTable
						.getColumn(partionField);
				if(null!=introspectedColumn){
					if (and) {
						sb.append("  and "); //$NON-NLS-1$
					} else {
						sb.append("where "); //$NON-NLS-1$
						and = true;
					}
					
					sb.append(MyBatis3FormattingUtilities
							.getEscapedColumnName(introspectedColumn));
					sb.append(" = "); //$NON-NLS-1$
					sb.append(MyBatis3FormattingUtilities
							.getParameterClause(introspectedColumn));
					answer.addElement(new TextElement(sb.toString()));
				}
			}
		}
	}
}
