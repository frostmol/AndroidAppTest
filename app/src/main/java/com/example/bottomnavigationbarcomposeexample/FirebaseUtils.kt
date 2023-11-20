package com.example.bottomnavigationbarcomposeexample

import android.util.Log
import com.google.firebase.database.*

data class SubjectDetails(
    val subjectName: String,
    val teacherName: String
)

data class StudentDetails(
    val uid: String, // Добавлено новое поле для хранения UID
    val fullName: String,
    val group: String?,
    val subjects: List<SubjectDetails>,
    val studentAssignments: List<StudentAssignment>
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
    val subjectName: String,
    val teacherName: String,
    val taskDescription: String,
    val status: String
)


data class TeacherAssignment(
    val assignmentId: String,
    val deadline: String,
    val subjectId: String,
    val groupName: String,
    val taskDescription: String
)

data class AssignmentDetails(
    val deadline: String,
    val subjectName: String,
    val teacherName: String,
    val taskDescription: String
)


// Update getStudentSubjects to use the new getSubjectNames function
private fun getStudentSubjects(groupId: String, onResult: (List<SubjectDetails>?) -> Unit) {
    val groupsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Groups")
    groupsReference.child(groupId).child("subjects").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val subjectIds = snapshot.children.mapNotNull { it.key }.toList()
            getSubjectNames(subjectIds) { subjectDetails ->
                onResult(subjectDetails)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult(null)
        }
    })
}




// Function to get subject names from the Subjects node
// Function to get subject names and teacher names from the Subjects node
private fun getSubjectNames(subjectIds: List<String>, onResult: (List<SubjectDetails>?) -> Unit) {
    val subjectsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Subjects")

    val subjectDetails = mutableListOf<SubjectDetails>()

    // Use a counter to track the number of subjects processed
    var subjectsProcessed = 0

    for (subjectId in subjectIds) {
        val subjectReference = subjectsReference.child(subjectId)

        subjectReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(subjectSnapshot: DataSnapshot) {
                val subjectName = subjectSnapshot.child("subject_name").getValue(String::class.java)
                val teacherId = subjectSnapshot.child("teacher_id").getValue(String::class.java)

                if (subjectName != null && teacherId != null) {
                    getTeacherFullName(teacherId) { teacherFullName ->
                        subjectDetails.add(SubjectDetails(subjectName, teacherFullName))

                        subjectsProcessed++
                        if (subjectsProcessed == subjectIds.size) {
                            // All subjects processed, invoke the callback
                            onResult(subjectDetails)
                        }
                    }
                } else {
                    // Handle the case where subject name or teacher ID is null
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                onResult(null)
            }
        })
    }
}



// Function to get teacher full name for a given teacherId
private fun getTeacherFullName(teacherId: String, onResult: (String) -> Unit) {
    val teacherReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(teacherId)

    teacherReference.child("full_name").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(teacherSnapshot: DataSnapshot) {
            val teacherFullName = teacherSnapshot.getValue(String::class.java) ?: ""
            onResult(teacherFullName)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult("") // Pass a default value in case of an error
        }
    })
}

// Function to get information about assignments for a student
private fun getStudentAssignments(uid: String, onResult: (List<StudentAssignment>?) -> Unit) {
    val studentAssignmentsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("StudentAssignments")

    studentAssignmentsReference.orderByChild("student_id").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val result = mutableListOf<StudentAssignment>()

            val assignmentsCount = snapshot.childrenCount
            var assignmentsProcessed = 0L

            for (childSnapshot in snapshot.children) {
                val assignmentId = childSnapshot.child("assignment_id").getValue(String::class.java) ?: ""
                val status = childSnapshot.child("status").getValue(String::class.java) ?: ""

                // Use functions to retrieve additional details
                getAssignmentDetails(assignmentId) { assignmentDetails ->
                    assignmentDetails?.let {
                        result.add(StudentAssignment(assignmentId, it.deadline, it.subjectName, it.teacherName, it.taskDescription, status))
                    }

                    assignmentsProcessed++
                    if (assignmentsProcessed == assignmentsCount) {
                        onResult(result)
                    }
                }
            }

            // Handle the case where there are no assignments
            if (assignmentsCount == 0L) {
                onResult(result)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult(null)
        }
    })
}


private fun getAssignmentDetails(assignmentId: String, onResult: (AssignmentDetails?) -> Unit) {
    val assignmentsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Assignments")

    assignmentsReference.child(assignmentId).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val deadline = snapshot.child("deadline").getValue(String::class.java) ?: ""
            val subjectId = snapshot.child("subject_id").getValue(String::class.java) ?: ""

            // Use the Subjects node to get the subject name
            getSubjectName(subjectId) { subjectName ->
                val teacherId = snapshot.child("teacher_id").getValue(String::class.java) ?: ""
                // Assuming you have a node for Users where teacher information is stored
                val teacherReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(teacherId)

                teacherReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(teacherSnapshot: DataSnapshot) {
                        val teacherName = teacherSnapshot.child("full_name").getValue(String::class.java) ?: ""
                        val taskDescription = snapshot.child("task_description").getValue(String::class.java) ?: ""

                        val assignmentDetails = AssignmentDetails(deadline, subjectName, teacherName, taskDescription)
                        onResult(assignmentDetails)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        onResult(null)
                    }
                })
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult(null)
        }
    })
}



// Function to get subject name from the Subjects node
private fun getSubjectName(subjectId: String, onResult: (String) -> Unit) {
    val subjectsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Subjects")
    val subjectNameSnapshot = subjectsReference.child(subjectId).child("subject_name")

    subjectNameSnapshot.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val subjectName = snapshot.getValue(String::class.java)
            onResult(subjectName ?: "")
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult("")
        }
    })
}




// Function to get teacher name for a given assignmentId
private fun getTeacherName(assignmentId: String, onResult: (String) -> Unit) {
    val assignmentsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Assignments")
    assignmentsReference.child(assignmentId).child("teacher_id").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val teacherId = snapshot.getValue(String::class.java) ?: ""
            val teacherReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(teacherId)
            teacherReference.child("full_name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(teacherSnapshot: DataSnapshot) {
                    val teacherName = teacherSnapshot.getValue(String::class.java) ?: ""
                    onResult(teacherName)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    onResult("") // Pass a default value in case of an error
                }
            })
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult("") // Pass a default value in case of an error
        }
    })
}

// Function to get task description for a given assignmentId
private fun getTaskDescription(assignmentId: String, onResult: (String) -> Unit) {
    val assignmentsReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Assignments")
    assignmentsReference.child(assignmentId).child("task_description").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val taskDescription = snapshot.getValue(String::class.java) ?: ""
            onResult(taskDescription)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            onResult("") // Pass a default value in case of an error
        }
    })
}

// Update getUserDetails function to use the new functions
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
                        Log.d("UserInfo", "Full Name: $fullName")
                        Log.d("UserInfo", "Group ID: $groupId")
                        Log.d("UserInfo", "Group Name: $groupName")

                        // Get subjects and assignments for the student
                        getStudentSubjects(groupId) { subjects ->
                            Log.d("UserInfo", "Subjects: $subjects")
                            getStudentAssignments(uid) { studentAssignments ->
                                Log.d("UserInfo", "Student Assignments: $studentAssignments")
                                onResult(StudentDetails(uid, fullName, groupName.orEmpty(), subjects.orEmpty(), studentAssignments.orEmpty()))


                            }
                        }

                    }
                }
                "teacher" -> {
                    TODO()
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



// Keep the getGroupName function unchanged
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
