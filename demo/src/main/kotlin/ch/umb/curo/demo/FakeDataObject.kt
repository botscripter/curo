package ch.umb.curo.demo

class FakeDataObject {

    val name: String = ""
    val inner: InnerFakeData = InnerFakeData()

    class InnerFakeData {
        val innerName: String = ""
    }

}
