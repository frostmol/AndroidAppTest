package com.example.bottomnavigationbarcomposeexample

import android.util.Log
import com.google.firebase.database.*

data class StudentDetails(
    val fullName: String,
    val group: String?,
    val studentAssignments: List<StudentAssignment>,
    val debts: List<String>
)

data class TeacherDetails(
    val fullName: String,
    val subjects: List<String>,
    val assignedGroups: List<String>,
    val teacherAssignments: List<TeacherAssignment>
)

data class StudentAssignment(
    val assignmentId: String,
    val deadline: String,
    val subjectId: String,
    val teacherName: String
)

data class TeacherAssignment(
    val assignmentId: String,
    val deadline: String,
    val subjectId: String,
    val groupName: String
)

// Функция для получения информации о пользователе
fun getUserDetails(uid: String, onResult: (Any?) -> Unit) {
    val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users/$uid")

    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val fullName = snapshot.child("full_name").getValue(String::class.java) ?: ""
            val role = snapshot.child("role").getValue(String::class.java) ?: ""

            when (role) {
                "student" -> {
                    val groupId = snapshot.child("group_id").getValue(String::class.java) ?: ""

                    // Get the group details using groupId
                    getGroupName(groupId) { groupName ->
                        Log.d("UserInfo", "Group ID: $groupId")
                        Log.d("UserInfo", "Group Name: $groupName")

                        // Assuming you have nodes for StudentAssignments and Debts
                        val studentAssignments = getStudentAssignments(snapshot.child("StudentAssignments"))
                        val debts = getDebts(snapshot.child("StudentAssignments"))

                        onResult(StudentDetails(fullName, groupName.orEmpty(), studentAssignments, debts))
                    }
                }
                "teacher" -> {
                    // Assuming you have nodes for Subjects, TeacherGroups, and Assignments
                    val subjects = getSubjects(snapshot.child("TeacherGroups"))
                    val assignedGroups = getAssignedGroups(snapshot.child("TeacherGroups"))
                    val teacherAssignments = getTeacherAssignments(snapshot.child("Assignments"), assignedGroups)

                    onResult(TeacherDetails(fullName, subjects, assignedGroups, teacherAssignments))
                }
                else -> {
                    onResult(null)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult(null)
        }
    }

    databaseReference.addValueEventListener(valueEventListener)
}

// Функция для получения информации о группе
private fun getGroupName(groupId: String, onResult: (String?) -> Unit) {
    val groupsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Groups")

    groupsReference.child(groupId).child("group_name").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val groupName = snapshot.getValue(String::class.java)
            onResult(groupName)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult(null)
        }
    })
}

// Функция для получения информации о заданиях студента
private fun getStudentAssignments(studentAssignmentsSnapshot: DataSnapshot): List<StudentAssignment> {
    val result = mutableListOf<StudentAssignment>()
    for (childSnapshot in studentAssignmentsSnapshot.children) {
        val assignmentId = childSnapshot.child("assignment_id").getValue(String::class.java) ?: ""
        val isCompleted = childSnapshot.getValue(Boolean::class.java) ?: false

        if (isCompleted) {
            getAssignmentDetails(assignmentId) { assignmentDetails ->
                if (assignmentDetails != null) {
                    result.add(assignmentDetails)
                }
            }
        }
    }
    return result
}

// Функция для получения информации о заданиях учителя
private fun getTeacherAssignments(assignmentsSnapshot: DataSnapshot, assignedGroups: List<String>): List<TeacherAssignment> {
    val result = mutableListOf<TeacherAssignment>()
    for (group in assignedGroups) {
        val groupAssignmentsSnapshot = assignmentsSnapshot.child(group)
        for (childSnapshot in groupAssignmentsSnapshot.children) {
            val assignmentId = childSnapshot.key ?: ""
            val deadline = childSnapshot.child("deadline").getValue(String::class.java) ?: ""
            val subjectId = childSnapshot.child("subject_id").getValue(String::class.java) ?: ""

            result.add(TeacherAssignment(assignmentId, deadline, subjectId, group))
        }
    }
    return result
}

// Функция для получения информации о долгах студента
private fun getDebts(studentAssignmentsSnapshot: DataSnapshot): List<String> {
    val result = mutableListOf<String>()
    for (childSnapshot in studentAssignmentsSnapshot.children) {
        val assignmentId = childSnapshot.child("assignment_id").getValue(String::class.java) ?: ""
        val isCompleted = childSnapshot.getValue(Boolean::class.java) ?: false

        if (!isCompleted) {
            result.add(assignmentId)
        }
    }
    return result
}

// Вспомогательная функция для получения дополнительных данных о задании
private fun getAssignmentDetails(assignmentId: String, onResult: (StudentAssignment?) -> Unit) {
    val assignmentsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Assignments")

    assignmentsReference.child(assignmentId).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val deadline = snapshot.child("deadline").getValue(String::class.java) ?: ""
            val subjectId = snapshot.child("subject_id").getValue(String::class.java) ?: ""
            val teacherId = snapshot.child("teacher_id").getValue(String::class.java) ?: ""

            // Assuming you have a node for Users where teacher information is stored
            val teacherReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(teacherId)

            teacherReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(teacherSnapshot: DataSnapshot) {
                    val teacherName = teacherSnapshot.child("full_name").getValue(String::class.java) ?: ""
                    onResult(StudentAssignment(assignmentId, deadline, subjectId, teacherName))
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    onResult(null)
                }
            })
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult(null)
        }
    })
}

// Функция для получения списка предметов учителя
private fun getSubjects(teacherGroupsSnapshot: DataSnapshot): List<String> {
    val result = mutableListOf<String>()
    for (childSnapshot in teacherGroupsSnapshot.children) {
        val subjectsSnapshot = childSnapshot.child("subjects")
        for (subjectSnapshot in subjectsSnapshot.children) {
            val subjectId = subjectSnapshot.key ?: ""
            result.add(subjectId)
        }
    }
    return result
}

// Функция для получения списка групп, к которым привязан учитель
private fun getAssignedGroups(teacherGroupsSnapshot: DataSnapshot): List<String> {
    val result = mutableListOf<String>()
    for (childSnapshot in teacherGroupsSnapshot.children) {
        val groupId = childSnapshot.child("group_id").getValue(String::class.java) ?: ""
        result.add(groupId)
    }
    return result
}