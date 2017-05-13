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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.PartitionUtil;
import org.mybatis.generator.config.ConditionField;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class SelectByConditionElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectByConditionElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$
        answer.addAttribute(new Attribute(
                "id", introspectedTable.getSelectByConditionStatementId())); //$NON-NLS-1$
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getBaseResultMapId()));
        }

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select "); //$NON-NLS-1$

        if (stringHasValue(introspectedTable
                .getSelectByPrimaryKeyQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
            sb.append("' as QUERYID,"); //$NON-NLS-1$
        }
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getBaseColumnListElement());
        if (introspectedTable.hasBLOBColumns()) {
            answer.addElement(new TextElement(",")); //$NON-NLS-1$
            answer.addElement(getBlobColumnListElement());
        }

        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        
        FullyQualifiedJavaType parameterType;
            parameterType = new FullyQualifiedJavaType(
                    introspectedTable.getBaseRecordType());

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterType.getFullyQualifiedName()));

        boolean and = false;

        for (ConditionField conditionField : introspectedTable.getTableConfiguration().getConditionFields()) {
        	IntrospectedColumn introspectedColumn  = introspectedTable.getColumn(conditionField.getName());
        	if(null!=introspectedColumn){
        		sb.setLength(0);
        		if (and) {
        			sb.append("  and "); //$NON-NLS-1$
        		} else {
        			sb.append("where "); //$NON-NLS-1$
        			and = true;
        		}
        		sb.append(MyBatis3FormattingUtilities
        				.getAliasedEscapedColumnName(introspectedColumn));
        		String leftString = (null!=conditionField.getLeftString()?conditionField.getLeftString():"");
        		String rightString = (null!=conditionField.getRightString()?conditionField.getRightString():"");
        		String operate = (null!=conditionField.getOperate()?conditionField.getOperate():"=");
        		sb.append(" "+operate+" "); //$NON-NLS-1$
        		String columnName = MyBatis3FormattingUtilities.getParameterClause(introspectedColumn);
        		if(operate.equals("like")){
        			columnName = "${"+introspectedColumn.getJavaProperty()+"}";
        		}
        		sb.append(leftString+columnName+rightString);
        		answer.addElement(new TextElement(sb.toString()));
        	}
        }
        
        //添加分库分表的路由字段设置
        PartitionUtil.addPartition(answer, introspectedTable, sb, and);

        parentElement.addElement(answer);
    }
}