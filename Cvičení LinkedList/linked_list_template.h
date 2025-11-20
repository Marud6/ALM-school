#ifndef LINKED_LIST_TEMPLATE_H
#define LINKED_LIST_TEMPLATE_H
#include <iostream>
using namespace std;

template<typename T>
class single_linked_list {
public:
    struct Node {
        T data;
        Node* next;
        Node* prev;
        Node(T d) : data(d), next(nullptr), prev(nullptr) {}
    };

private:
    Node* head;
    Node* tail;

public:
    single_linked_list() : head(nullptr), tail(nullptr) {}


    void traverse() {
        Node* curr = head;
        while (curr) {
            cout << curr->data << "->";
            curr = curr->next;
        }
        cout << endl;
    }

    Node* addFirst(T value) {
        Node* n = new Node(value);
        if (!head) head = tail = n;
        else {
            n->next = head;
            head->prev = n;
            head = n;
        }
        return n;
    }

    Node* addLast(T value) {
        Node* n = new Node(value);
        if (!tail) head = tail = n;
        else {
            tail->next = n;
            n->prev = tail;
            tail = n;
        }
        return n;
    }

    Node* addAfter(Node* current, T value) {
        if (!current) return nullptr;
        Node* n = new Node(value);
        n->next = current->next;
        n->prev = current;
        if (current->next) current->next->prev = n;
        else tail = n;
        current->next = n;
        return n;
    }

    Node* addBefore(Node* current, T value) {
        if (!current) return nullptr;
        Node* n = new Node(value);
        n->prev = current->prev;
        n->next = current;
        if (current->prev) current->prev->next = n;
        else head = n;
        current->prev = n;
        return n;
    }

    Node* first() {
      return head;

    }
    Node* last() {
        return tail;
    }

    Node* deleteNode(Node* current) {
        if (!current) return nullptr;
        Node* prev = current->prev;
        Node* next = current->next;
        if (prev) prev->next = next;
        else head = next;
        if (next) next->prev = prev;
        else tail = prev;
        delete current;
        return head;
    }

    void removeFirst() {
        if (!head) return;
        Node* temp = head;
        head = head->next;
        if (head) head->prev = nullptr;
        else tail = nullptr;
        delete temp;
    }

    void removeLast() {
        if (!tail) return;
        Node* temp = tail;
        tail = tail->prev;
        if (tail) tail->next = nullptr;
        else head = nullptr;
        delete temp;
    }


};

#endif
