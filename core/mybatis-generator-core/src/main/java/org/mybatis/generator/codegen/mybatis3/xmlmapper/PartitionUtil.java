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

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.PartitionField;

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
	public static void addPartition(XmlElement answer,
			IntrospectedTable introspectedTable, StringBuilder sb, boolean and) {
		addPartitionField(answer,introspectedTable,sb,and);
		addPartitionParameterType(answer,introspectedTable,sb,and);
	}
	
	public static void addPartitionField(XmlElement answer,
			IntrospectedTable introspectedTable, StringBuilder sb, boolean and) {
		List<PartitionField> list = introspectedTable.getTableConfiguration().getPartitionFields();
		if (null != list && list.size() > 0) {
			for (PartitionField partionFieldE : list) {
				String partionField = partionFieldE.getName();
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
	
	public static void addPartitionParameterType(XmlElement answer,
			IntrospectedTable introspectedTable, StringBuilder sb, boolean and) {

		String parameterType = introspectedTable.getTableConfiguration()
				.getParameterType();
		List<PartitionField> list = introspectedTable.getTableConfiguration().getPartitionFields();
		if((null!=list && list.size() > 0 ) || null!=parameterType){
			if(null==parameterType){
				FullyQualifiedJavaType parameterTypeClass = introspectedTable.getRules()
						.calculateAllFieldsClass();
				parameterType = null!=parameterTypeClass.getFullyQualifiedName()?parameterTypeClass.getFullyQualifiedName():"map";
			}
			answer.removeAttribute("parameterType");
			answer.addAttribute(new Attribute("parameterType", parameterType));
		}
		
	}
	
	public static void addPartitionJavaMethodParameterType(Method method,
			IntrospectedTable introspectedTable) {
		String parameterType = introspectedTable.getTableConfiguration()
				.getParameterType();
		List<PartitionField> list = introspectedTable.getTableConfiguration().getPartitionFields();
		if((null!=list && list.size() > 0) || null !=parameterType){
			String classType;
			if(null!=parameterType){
				classType = parameterType;
			}else{
				if (introspectedTable.getRules().generatePrimaryKeyClass()) {
					classType = introspectedTable.getPrimaryKeyType();
				} else {
			        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
			        	classType = introspectedTable.getRecordWithBLOBsType();
			        } else {
			        	classType = introspectedTable.getBaseRecordType();
			        }
				}
			}
			method.getParameters().removeAll(method.getParameters());
			Parameter parameter = new Parameter(new FullyQualifiedJavaType(classType), "record");
			method.addParameter(parameter);
		}
		
	}
}
