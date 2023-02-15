package com.jocoos.mybeautip.domain.broadcast.service.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ViewerUsernameUtilTest {

    @Test
    fun generateSortedUsername() {
        val managerKorean = ViewerUsernameUtil.generateSortedUsername("매니저");
        Assertions.assertEquals("0|매니저", managerKorean)
        val managerEnglish = ViewerUsernameUtil.generateSortedUsername("A매니저");
        Assertions.assertEquals("1|A매니저", managerEnglish)
        val managerNumber = ViewerUsernameUtil.generateSortedUsername("0매니저");
        Assertions.assertEquals("2|0매니저", managerNumber)
        val managerChar = ViewerUsernameUtil.generateSortedUsername("_매니저");
        Assertions.assertEquals("3|_매니저", managerChar)

        val memberKorean = ViewerUsernameUtil.generateSortedUsername("회원");
        Assertions.assertEquals("0|회원", memberKorean)
        val memberEnglish = ViewerUsernameUtil.generateSortedUsername("A회원");
        Assertions.assertEquals("1|A회원", memberEnglish)
        val memberNumber = ViewerUsernameUtil.generateSortedUsername("0회원");
        Assertions.assertEquals("2|0회원", memberNumber)
        val memberChar = ViewerUsernameUtil.generateSortedUsername("_회원");
        Assertions.assertEquals("3|_회원", memberChar)

        val guest = ViewerUsernameUtil.generateSortedUsername("GUEST_0001");
        Assertions.assertEquals("1|GUEST_0001", guest)
    }

    @Test
    fun testCharType() {
        var testChar: Char = '-'
        println(testChar + " >> " + Character.getType(testChar))
        testChar = '_'
        println(testChar + " >> " + Character.getType(testChar))

        testChar = '0'
        println(testChar + " >> " + Character.getType(testChar))
        testChar = '9'
        println(testChar + " >> " + Character.getType(testChar))

        testChar = 'a'
        println(testChar + " >> " + Character.getType(testChar))
        testChar = 'Z'
        println(testChar + " >> " + Character.getType(testChar))

        testChar = '가'
        println(testChar + " >> " + Character.getType(testChar))
        testChar = '힣'
        println(testChar + " >> " + Character.getType(testChar))
    }
}